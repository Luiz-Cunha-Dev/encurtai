package com.encurtador.redirect_service.config;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.rabbitmq.RabbitFlux;
import reactor.rabbitmq.Sender;
import reactor.rabbitmq.SenderOptions;
import reactor.util.retry.Retry;

import java.time.Duration;

@Configuration
@Slf4j
@Profile("!test")
public class RabbitConfig {
    @Value("${spring.rabbitmq.host}") private String host;
    @Value("${spring.rabbitmq.port}") private int port;
    @Value("${spring.rabbitmq.username}") private String username;
    @Value("${spring.rabbitmq.password}") private String password;
    @Value("${spring.rabbitmq.virtual-host}") private String virtualHost;

    @Bean
    Mono<Connection> connectionMono() {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(host);
        connectionFactory.setPort(port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setVirtualHost(virtualHost);
        connectionFactory.useNio();
        return Mono.fromCallable(() -> connectionFactory.newConnection("reactor-rabbit"))
                .subscribeOn(Schedulers.boundedElastic())
                .cache()
                .doOnSubscribe(sub -> log.info("Establishing RabbitMQ connection..."))
                .doOnError(err -> log.error("Failed to establish RabbitMQ connection", err))
                .doOnSuccess(conn -> log.info("RabbitMQ connection established successfully"))
                .retryWhen(Retry.backoff(10, Duration.ofSeconds(1))
                        .maxBackoff(Duration.ofSeconds(30))
                        .doBeforeRetry(rs -> log.warn("Retrying RabbitMQ connection... Attempt: {}", rs.totalRetries() + 1))
                );
    }

    @Bean
    public SenderOptions senderOptions(Mono<Connection> connectionMono) {
        return new SenderOptions()
                .connectionMono(connectionMono)
                .resourceManagementScheduler(Schedulers.boundedElastic())
                .connectionSubscriptionScheduler(Schedulers.boundedElastic());
    }

    @Bean
    public Sender sender(SenderOptions senderOptions) {
        return RabbitFlux.createSender(senderOptions);
    }
}
