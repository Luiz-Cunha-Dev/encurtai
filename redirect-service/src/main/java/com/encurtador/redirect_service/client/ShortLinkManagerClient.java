package com.encurtador.redirect_service.client;

import com.encurtador.redirect_service.dto.GetMainUrlResponseDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class ShortLinkManagerClient {

    private final WebClient webClient;

    @CircuitBreaker(name = "shortLinkManager", fallbackMethod = "fallbackGetMainUrl")
    public Mono<GetMainUrlResponseDto> getMainUrl(String token) {
        return webClient.get()
                .uri("http://encurta-ai-consul:8600/sms/{token}", token)
                .retrieve()
                .bodyToMono(GetMainUrlResponseDto.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(2)));
    }

    public Mono<GetMainUrlResponseDto> fallbackGetMainUrl(String token, Throwable t) {
        return Mono.error(new RuntimeException("Failed to fetch main URL for token: " + token, t));
    }
}

// lib testes
/// unitario junit mockito
// integracao/e2e teste do controller como um fluxo, ou seja, vai ser chamado o endpoint
// usa junit, mockito e WireMock (mockar short link managar client response)
//dockerfile