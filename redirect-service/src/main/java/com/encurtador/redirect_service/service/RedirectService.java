package com.encurtador.redirect_service.service;

import com.encurtador.redirect_service.client.ShortLinkManagerClient;
import com.encurtador.redirect_service.dto.GetMainUrlResponseDto;
import com.encurtador.redirect_service.producer.RedirectCountProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class RedirectService {
    private final ShortLinkManagerClient shortLinkManagerClient;
    private final RedirectCountProducer redirectCountProducer;

    public Mono<String> getMainUrl(String shortUrlKey) {
        return shortLinkManagerClient.getMainUrl(shortUrlKey)
                .map(GetMainUrlResponseDto::mainUrl)
                .doOnSuccess(mainUrl -> {
                    String eventMessage = String.format("{\"shortUrlKey\":\"%s\"}", shortUrlKey);
                    redirectCountProducer.publish(eventMessage);
                });
    }
}
