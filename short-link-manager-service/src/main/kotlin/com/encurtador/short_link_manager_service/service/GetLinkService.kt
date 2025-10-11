package com.encurtador.short_link_manager_service.service

import com.encurtador.short_link_manager_service.dto.LinkResponseDto
import com.encurtador.short_link_manager_service.repository.ShortLinkManagerRepository
import org.springframework.stereotype.Service

@Service
class GetLinkService(
    private val repository: ShortLinkManagerRepository
) {
    fun execute(key: String): LinkResponseDto {
        val link = repository.get(key) ?: throw NoSuchElementException("Link not found")
        return LinkResponseDto(
            mainUrl = link.mainUrl,
            shortenedUrl = link.shortenedUrl
        )
    }
}
