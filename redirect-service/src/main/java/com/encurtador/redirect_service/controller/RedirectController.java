package com.encurtador.redirect_service.controller;

import com.encurtador.redirect_service.service.RedirectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/{shortUrlKey}")
@RequiredArgsConstructor
@Slf4j
public class RedirectController {
    private final RedirectService redirectService;

    @GetMapping
    public Mono<ResponseEntity<Object>> redirect(@PathVariable String shortUrlKey) {
        return redirectService.getMainUrl(shortUrlKey)
                .flatMap(url -> {
                    HttpHeaders headers = new HttpHeaders();
                    headers.add("Location", url);
                    return Mono.just(ResponseEntity.status(302).headers(headers).build());
                })
                .doOnSubscribe(s -> log.info("Received redirect request for short URL key: {}", shortUrlKey))
                .doOnError(e -> log.error("Error processing redirect for short URL key: {}", shortUrlKey, e))
                .doOnSuccess(response -> log.info("Redirecting to main URL for short URL key: {}", shortUrlKey));
    }
}
