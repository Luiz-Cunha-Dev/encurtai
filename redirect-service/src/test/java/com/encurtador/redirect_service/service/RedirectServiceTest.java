package com.encurtador.redirect_service.service;

import com.encurtador.redirect_service.client.ShortLinkManagerClient;
import com.encurtador.redirect_service.dto.GetMainUrlResponseDto;
import com.encurtador.redirect_service.producer.RedirectCountProducer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RedirectServiceTest {
    @Mock private ShortLinkManagerClient shortLinkManagerClient;
    @Mock private RedirectCountProducer redirectCountProducer;
    @InjectMocks private RedirectService redirectService;

    @Test
    void shouldGetMainUrlAndPublishEvent() {
        when(shortLinkManagerClient.getMainUrl("abc123"))
            .thenReturn(Mono.just(new GetMainUrlResponseDto("https://example.com")));
        doNothing().when(redirectCountProducer).publish("{\"shortUrlKey\":\"abc123\"}");

        StepVerifier.create(redirectService.getMainUrl("abc123"))
                .expectNext("https://example.com")
                .verifyComplete();

        verify(shortLinkManagerClient).getMainUrl("abc123");
        verify(redirectCountProducer).publish("{\"shortUrlKey\":\"abc123\"}");
    }

    @Test
    void shouldNotPublishEventOnError() {
        when(shortLinkManagerClient.getMainUrl("invalidKey"))
            .thenReturn(Mono.error(new RuntimeException("Not found")));

        StepVerifier.create(redirectService.getMainUrl("invalidKey"))
                .expectError()
                .verify();

        verify(shortLinkManagerClient).getMainUrl("invalidKey");
        verifyNoInteractions(redirectCountProducer);
    }
}
