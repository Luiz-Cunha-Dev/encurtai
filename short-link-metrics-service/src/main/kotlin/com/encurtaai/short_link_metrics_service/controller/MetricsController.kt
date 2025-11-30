package com.encurtaai.short_link_metrics_service.controller

import com.encurtaai.short_link_metrics_service.model.RedirectCountDto
import com.encurtaai.short_link_metrics_service.service.GetRedirectCountService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/metrics")
class MetricsController(
    private val getRedirectCountService: GetRedirectCountService
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    @GetMapping("/{token}")
    @ResponseStatus(HttpStatus.OK)
    fun getRedirectCount(@PathVariable token: String): Mono<RedirectCountDto> =
        getRedirectCountService.execute(token)
            .doOnSubscribe {
                log.info("Fetching redirect count for token: {}", token)
            }
            .doOnSuccess {
                log.info("Successfully fetched redirect count for token: {}", token)
            }
            .doOnError { ex ->
                log.error("Error fetching redirect count for token: {}", token, ex)
            }
}
