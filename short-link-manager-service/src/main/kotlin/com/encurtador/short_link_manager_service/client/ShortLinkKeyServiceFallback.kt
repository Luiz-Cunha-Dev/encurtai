package com.encurtador.short_link_manager_service.client

import com.encurtador.short_link_manager_service.dto.ShortLinkKeyResponseDto
import org.springframework.stereotype.Component

@Component
class ShortLinkKeyServiceFallback : ShortLinkKeyServiceClient {

    override fun getUniqueTokenId(): ShortLinkKeyResponseDto {
      throw IllegalStateException("ShortLinkKeyService is currently unavailable. Please try again later.")
    }
}