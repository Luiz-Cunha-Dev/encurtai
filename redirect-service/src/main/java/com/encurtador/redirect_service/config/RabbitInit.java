package com.encurtador.redirect_service.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import reactor.rabbitmq.BindingSpecification;
import reactor.rabbitmq.ExchangeSpecification;
import reactor.rabbitmq.QueueSpecification;
import reactor.rabbitmq.Sender;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
@Profile("!test")
public class RabbitInit {
    private final Sender sender;

    @EventListener
    public void onApplicationReady(ContextRefreshedEvent event) {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", "redirect-exchange-dlq");
        args.put("x-dead-letter-routing-key", "redirect.count");

        sender.declareExchange(ExchangeSpecification.exchange("redirect-exchange-dlq").type("direct").durable(true))
                .then(sender.declareQueue(QueueSpecification.queue("redirect-queue-dlq").durable(true)))
                .then(sender.bind(BindingSpecification.binding("redirect-exchange-dlq", "redirect.count", "redirect-queue-dlq")))
                .then(sender.declareExchange(ExchangeSpecification.exchange("redirect-exchange").type("direct").durable(true)))
                .then(sender.declareQueue(QueueSpecification.queue("redirect-queue").durable(true).arguments(args)))
                .then(sender.bind(BindingSpecification.binding("redirect-exchange", "redirect.count", "redirect-queue")))
                .doOnSubscribe(sub -> log.info("Declaring RabbitMQ exchange, queue, and binding..."))
                .doOnError(err -> log.error("Failed to declare RabbitMQ exchange, queue, or binding", err))
                .doOnSuccess(v -> log.info("RabbitMQ exchange, queue, and binding declared successfully"))
                .subscribe();
    }
}
