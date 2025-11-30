package com.encurtaai.short_link_metrics_service.config

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import reactor.core.publisher.Mono

@ControllerAdvice
class GlobalExceptionHandler {
    private val log = LoggerFactory.getLogger(this::class.java)

    @ExceptionHandler(Exception::class)
    fun handleRuntimeException(ex: RuntimeException): Mono<ResponseEntity<Map<String, Any>>> {
        log.error("Exception: ", ex)

        val error = mutableMapOf<String, Any>(
            "timestamp" to System.currentTimeMillis(),
        )

        if (ex.message == "Token not found") {
            error["status"] = HttpStatus.NOT_FOUND.value()
            error["error"] = "Not Found"
            error["message"] = ex.message ?: "Token not found"

            return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(error))
        }

        error["status"] = HttpStatus.INTERNAL_SERVER_ERROR.value()
        error["error"] = "Internal Server Error"
        error["message"] = "An unexpected error occurred"


        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error))
    }
}
