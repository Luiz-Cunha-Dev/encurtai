package com.encurtaai.short_link_metrics_service.service

import com.encurtaai.short_link_metrics_service.model.RedirectCountDto
import com.encurtaai.short_link_metrics_service.repository.MetricsRepository
import org.springframework.stereotype.Service

@Service
class GetRedirectCountService (
    private val metricsRepository: MetricsRepository
) {
    fun execute(token: String) =
        metricsRepository
            .getClickCount(token)
            .map { RedirectCountDto(it) }
}
