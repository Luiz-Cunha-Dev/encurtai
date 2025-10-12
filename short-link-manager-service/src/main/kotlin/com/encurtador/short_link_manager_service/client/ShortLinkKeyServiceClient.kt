package com.encurtador.short_link_manager_service.client

import com.encurtador.short_link_manager_service.dto.ShortLinkKeyResponseDto
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping

@FeignClient(name = "short-link-key-service",
    url = "\${encurta-ai.short-link-key-service.url}",
    fallback = ShortLinkKeyServiceFallback::class)
interface ShortLinkKeyServiceClient {

    @GetMapping
    fun getUniqueTokenId(): ShortLinkKeyResponseDto
}