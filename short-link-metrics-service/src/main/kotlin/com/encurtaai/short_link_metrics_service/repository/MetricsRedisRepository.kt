package com.encurtaai.short_link_metrics_service.repository

import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class MetricsRedisRepository(
    val reactiveRedisTemplate: ReactiveStringRedisTemplate
): MetricsRepository {
    override fun getClickCount(token: String) =
        reactiveRedisTemplate.opsForValue()
            .get(token)
            .map { it.toLong() }
            .switchIfEmpty(Mono.defer { Mono.error(RuntimeException("Token not found")) })

    override fun incrementClickCount(token: String) =
        reactiveRedisTemplate.opsForValue()
            .increment(token)
            .then()
}