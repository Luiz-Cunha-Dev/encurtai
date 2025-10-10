package com.encurtador.redirect_service.controller;

import com.encurtador.redirect_service.service.RedirectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/r")
@RequiredArgsConstructor
public class RedirectController {

    private final RedirectService redirectService;

    //criar global exception handler

    @GetMapping("/{shortUrlKey}")
    public Mono<ResponseEntity<Void>> redirect(@PathVariable String shortUrlKey) {
        return redirectService.getMainUrl(shortUrlKey)
                .flatMap(url -> {
                    HttpHeaders headers = new HttpHeaders();
                    headers.add("Location", url);
                    return Mono.just(ResponseEntity.status(302).headers(headers).build());
                });
    }
}

