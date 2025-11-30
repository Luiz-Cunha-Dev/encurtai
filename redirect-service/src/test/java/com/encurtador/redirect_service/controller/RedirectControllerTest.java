package com.encurtador.redirect_service.controller;

import com.encurtador.redirect_service.producer.RedirectCountProducer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.github.tomakehurst.wiremock.client.WireMock.reset;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
public class RedirectControllerTest {
    @Autowired private WebTestClient webTestClient;
    @MockitoBean private RedirectCountProducer redirectCountProducer;

    @BeforeEach
    void setup() {
        reset();
    }

    @Test
    void shouldRedirectToMainUrl() {
        String shortUrlKey = "abc123";
        String mainUrl = "https://example.com/main-page";

        mockShortLinkManagerGetMainUrl(shortUrlKey, mainUrl);

        webTestClient
                .get()
                .uri("/{shortUrlKey}", shortUrlKey)
                .exchange()
                .expectStatus().isFound()
                .expectHeader().valueEquals("Location", mainUrl);
    }

    private void mockShortLinkManagerGetMainUrl(String shortUrlKey, String mainUrl) {
        String responseBody = String.format("{\"mainUrl\":\"%s\"}", mainUrl);

        WireMock.stubFor(
                WireMock.get(WireMock.urlEqualTo("/" + shortUrlKey))
                        .willReturn(
                                WireMock.aResponse()
                                        .withHeader("Content-Type", "application/json")
                                        .withBody(responseBody)
                                        .withStatus(200)
                        )
        );
    }
}
