package com.encurtador.short_link_manager_service

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ShortLinkManagerServiceApplication

fun main(args: Array<String>) {
	runApplication<ShortLinkManagerServiceApplication>(*args)
}
