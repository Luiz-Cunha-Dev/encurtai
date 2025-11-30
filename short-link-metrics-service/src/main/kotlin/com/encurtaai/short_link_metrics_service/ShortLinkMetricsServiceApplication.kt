package com.encurtaai.short_link_metrics_service

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ShortLinkMetricsServiceApplication

fun main(args: Array<String>) {
	runApplication<ShortLinkMetricsServiceApplication>(*args)
}
