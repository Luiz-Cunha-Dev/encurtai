package com.encurtaai.short_link_metrics_service.service

import com.encurtaai.short_link_metrics_service.repository.MetricsRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

internal class GetRedirectCountServiceTest {
    private val metricsRepository = mockk<MetricsRepository>()
    private val getRedirectCountService = GetRedirectCountService(metricsRepository)

    @Test
    fun `should get redirect count`() {
        every {
            metricsRepository.getClickCount("token123")
        } returns Mono.just(42L)

        StepVerifier.create(getRedirectCountService.execute("token123"))
            .expectNextMatches { dto -> dto.count == 42L }
            .verifyComplete()

        verify(exactly = 1) { metricsRepository.getClickCount("token123") }
    }
}