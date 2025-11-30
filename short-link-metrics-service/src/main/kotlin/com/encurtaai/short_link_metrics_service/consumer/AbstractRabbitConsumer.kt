package com.encurtaai.short_link_metrics_service.consumer

import org.slf4j.LoggerFactory
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.rabbitmq.AcknowledgableDelivery
import reactor.rabbitmq.Receiver
import reactor.util.retry.Retry
import java.time.Duration

abstract class AbstractRabbitConsumer(
    protected val receiver: Receiver
) {
    private val log = LoggerFactory.getLogger(this::class.java)
    protected abstract fun getQueue(): String
    protected abstract fun processMessage(message: ByteArray): Mono<Void>

    fun consume(autoAck: Boolean = true, retryAttempts: Long = 5): Flux<Void> {
        val flux = if (autoAck) {
            receiver.consumeAutoAck(getQueue())
        } else {
            receiver.consumeManualAck(getQueue())
        }

        return flux.flatMap { delivery ->
            processMessage(delivery.body)
                .doOnSuccess {
                    if (!autoAck && delivery is AcknowledgableDelivery) delivery.ack()
                    log.info("Message processed successfully from queue: {}", getQueue())
                }
                .doOnError { e ->
                    log.warn("Processing failed for message from queue: {}. Will retry if attempts left", getQueue(), e)
                }
                .retryWhen(
                    Retry.backoff(retryAttempts, Duration.ofMillis(500))
                        .maxBackoff(Duration.ofSeconds(5))
                        .doBeforeRetry { rs ->
                            log.warn(
                                "Retrying message from queue: {}. Attempt {}",
                                getQueue(),
                                rs.totalRetries() + 1
                            )
                        }
                )
                .onErrorResume { e ->
                    if (!autoAck && delivery is AcknowledgableDelivery) {
                        delivery.nack(false)
                    }
                    log.error("Message failed after retries. Sent to DLQ queue: {}", getQueue(), e)
                    Mono.empty()
                }
        }
    }

    fun start(autoAck: Boolean = true, retryAttempts: Long = 5) {
        consume(autoAck, retryAttempts)
            .subscribe(
                {},
                { err -> log.error("Unexpected error in consumer: {}", getQueue(), err) },
                { log.info("Consumer started for queue: {}", getQueue()) }
            )
    }
}
