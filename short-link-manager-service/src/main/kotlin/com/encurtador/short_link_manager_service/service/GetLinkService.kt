package com.encurtador.short_link_manager_service.service

import com.encurtador.short_link_manager_service.dto.LinkResponseDto
import com.encurtador.short_link_manager_service.repository.ShortLinkManagerMongoRepository
import org.springframework.stereotype.Service

@Service
class GetLinkService(
    private val repository: ShortLinkManagerMongoRepository
) {
    fun execute(key: String): LinkResponseDto {
        val link = repository.findById(key).orElseThrow { NoSuchElementException("Link not found") }
        return LinkResponseDto(
            mainUrl = link.mainUrl,
            shortenedUrl = link.shortenedUrl
        )
    }
}
