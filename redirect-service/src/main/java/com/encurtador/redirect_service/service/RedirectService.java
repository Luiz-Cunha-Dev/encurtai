package com.encurtador.redirect_service.service;

import com.encurtador.redirect_service.client.ShortLinkManagerClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class RedirectService {

    private final ShortLinkManagerClient shortLinkManagerClient;

    public Mono<String> getMainUrl(String shortUrlKey) {
        return shortLinkManagerClient.getMainUrl(shortUrlKey)
                .map(response -> response.mainUrl());
    }
}
