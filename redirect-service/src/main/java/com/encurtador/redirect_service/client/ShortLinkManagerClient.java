package com.encurtador.redirect_service.client;

import com.encurtador.redirect_service.dto.GetMainUrlResponseDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Component
@RequiredArgsConstructor
@Slf4j
public class ShortLinkManagerClient {

    private final WebClient webClient;

    @Value("${encurta-ai.short-link-manager.url}")
    private String shortLinkManagerUrl;

    @CircuitBreaker(name = "shortLinkManager", fallbackMethod = "fallbackGetMainUrl")
    public Mono<GetMainUrlResponseDto> getMainUrl(String token) {
        final String url = shortLinkManagerUrl + "/{token}";
        return webClient.get()
                .uri(url, token)
                .retrieve()
                .bodyToMono(GetMainUrlResponseDto.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(2)))
                .doOnSubscribe(s -> log.info("Fetching main URL for token: {}", token))
                .doOnSuccess(response -> log.info("Successfully fetched main URL for token: {}, response: {}", token, response))
                .doOnError(e -> log.error("Error fetching main URL for token: {}", token, e));
    }

    public Mono<GetMainUrlResponseDto> fallbackGetMainUrl(String token, Throwable t) {
        return Mono.error(new RuntimeException("Failed to fetch main URL for token: " + token, t));
    }
}
