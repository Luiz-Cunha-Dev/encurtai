package com.encurtador.redirect_service.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.rabbitmq.OutboundMessage;
import reactor.rabbitmq.Sender;
import reactor.util.retry.Retry;

import java.time.Duration;

@RequiredArgsConstructor
@Slf4j
public abstract class AbstractRabbitProducer {
    protected final Sender sender;
    protected abstract String getExchange();
    protected abstract String getRoutingKey();

    protected Mono<Void> send(byte[] payload) {
        OutboundMessage message = new OutboundMessage(getExchange(), getRoutingKey(), payload);

        return sender.sendWithPublishConfirms(Mono.just(message))
                .flatMap(confirm -> {
                    if (confirm.isAck()) {
                        log.info("Message sent successfully to exchange: {}, routingKey: {}", getExchange(), getRoutingKey());
                        return Mono.empty();
                    } else {
                        log.error("Message NACKed by broker for exchange: {}, routingKey: {}", getExchange(), getRoutingKey());
                        return Mono.error(new RuntimeException("Message NACKed by broker"));
                    }
                })
                .then()
                .retryWhen(Retry.backoff(getMaxRetries(), Duration.ofMillis(500))
                        .maxBackoff(Duration.ofSeconds(5))
                        .doBeforeRetry(rs -> log.warn("Retrying to send message to exchange: {}, routingKey: {}. Attempt: {}",
                                getExchange(), getRoutingKey(), rs.totalRetries() + 1))
                );
    }

    protected int getMaxRetries() {
        return 5;
    }

    public Mono<Void> publishEvent(String message) {
        return send(message.getBytes())
                .doOnSubscribe(s -> log.info("Publishing event to exchange: {}, routingKey: {}", getExchange(), getRoutingKey()))
                .doOnError(e -> log.error("Failed to publish event", e))
                .doOnSuccess(v -> log.info("Event published successfully to exchange: {}, routingKey: {}", getExchange(), getRoutingKey()));
    }
}
