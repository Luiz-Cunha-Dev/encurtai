package com.encurtaai.short_link_metrics_service.config

import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import reactor.rabbitmq.RabbitFlux
import reactor.rabbitmq.Receiver
import reactor.rabbitmq.ReceiverOptions
import reactor.util.retry.Retry
import java.time.Duration

@Configuration
class RabbitConfig(
    @Value("\${spring.rabbitmq.host}") private val host: String,
    @Value("\${spring.rabbitmq.port}") private val port: Int,
    @Value("\${spring.rabbitmq.username}") private val username: String,
    @Value("\${spring.rabbitmq.password}") private val password: String,
    @Value("\${spring.rabbitmq.virtual-host}") private val virtualHost: String
) {
    private val log = LoggerFactory.getLogger(RabbitConfig::class.java)

    @Bean
    fun connectionMono(): Mono<Connection> {
        val factory = ConnectionFactory()
        factory.host = host
        factory.port = port
        factory.username = username
        factory.password = password
        factory.virtualHost = virtualHost
        factory.useNio()
        return Mono.fromCallable { factory.newConnection("reactor-rabbit") }
            .subscribeOn(Schedulers.boundedElastic())
            .cache()
            .doOnSubscribe { log.info("Attempting to connect to RabbitMQ...") }
            .doOnError { ex -> log.error("Error connecting to RabbitMQ: ${ex.message}", ex) }
            .doOnSuccess { log.info("Successfully connected to RabbitMQ") }
            .retryWhen(
                Retry.backoff(10, Duration.ofSeconds(1))
                    .maxBackoff(Duration.ofSeconds(30))
                    .doBeforeRetry { rs ->
                        log.warn("Failed to connect to RabbitMQ. Retry #${rs.totalRetries() + 1}")
                    }
            )
    }

    @Bean
    fun receiverOptions(connectionMono: Mono<Connection>): ReceiverOptions =
        ReceiverOptions()
            .connectionMono(connectionMono)
            .connectionSubscriptionScheduler(Schedulers.boundedElastic())

    @Bean
    fun receiver(receiverOptions: ReceiverOptions): Receiver =
        RabbitFlux.createReceiver(receiverOptions)
}
