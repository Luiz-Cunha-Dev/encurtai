package com.encurtaai.short_link_metrics_service.repository

import reactor.core.publisher.Mono

interface MetricsRepository {
    fun getClickCount(token: String): Mono<Long>
    fun incrementClickCount(token: String): Mono<Void>
}