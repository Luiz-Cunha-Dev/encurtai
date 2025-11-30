package com.encurtaai.short_link_metrics_service.consumer

import com.encurtaai.short_link_metrics_service.model.RedirectCountMessage
import com.encurtaai.short_link_metrics_service.repository.MetricsRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.rabbitmq.Receiver

@Component
class RedirectCountConsumer(
    receiver: Receiver,
    private val objectMapper: ObjectMapper,
    private val metricsRepository: MetricsRepository,
) : AbstractRabbitConsumer(receiver) {
    private val log = LoggerFactory.getLogger(this::class.java)
    override fun getQueue(): String = "redirect-queue"

    init { start(autoAck = false, retryAttempts = 5) }

    override fun processMessage(message: ByteArray): Mono<Void> {
        val msg = objectMapper.readValue(message, RedirectCountMessage::class.java)

        return metricsRepository
            .incrementClickCount(msg.shortUrlKey)
            .doOnSuccess {
                log.info("Incremented click count for token: {}", msg.shortUrlKey)
            }
            .doOnError { ex ->
                log.error("Failed to increment click count for token: {}", msg.shortUrlKey, ex)
            }
    }
}
