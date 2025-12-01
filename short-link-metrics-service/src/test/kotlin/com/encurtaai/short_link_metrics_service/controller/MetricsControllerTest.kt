package com.encurtaai.short_link_metrics_service.controller

import com.encurtaai.short_link_metrics_service.config.RabbitConfig
import com.encurtaai.short_link_metrics_service.consumer.AbstractRabbitConsumer
import com.encurtaai.short_link_metrics_service.repository.MetricsRepository
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
internal class MetricsControllerTest {
    @Autowired private lateinit var webTestClient: WebTestClient
    @MockkBean private lateinit var metricsRepository: MetricsRepository
    @MockitoBean private lateinit var rabbitConfig: RabbitConfig
    @MockitoBean private lateinit var abstractRabbitConsumer: AbstractRabbitConsumer

    @Test
    fun `should get redirect count successfully`() {
        every {
            metricsRepository.getClickCount("abc123")
        } returns Mono.just(82L)

        webTestClient
            .get()
            .uri("/metrics/abc123")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.count").isEqualTo(82)

        verify(exactly = 1) { metricsRepository.getClickCount("abc123") }
    }
}