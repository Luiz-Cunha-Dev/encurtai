package com.encurtador.short_link_manager_service.service

import com.encurtador.short_link_manager_service.dto.GetAllLinksResponseDto
import com.encurtador.short_link_manager_service.dto.LinkResponseDto
import com.encurtador.short_link_manager_service.dto.PaginationResponseDto
import com.encurtador.short_link_manager_service.dto.ShortenLinkResponseDto
import com.encurtador.short_link_manager_service.model.ShortenedLink
import com.encurtador.short_link_manager_service.repository.ShortLinkManagerRepository
import org.springframework.stereotype.Service

@Service
class GetAllLinksService(
    private val repository: ShortLinkManagerRepository
) {
    fun execute(page: Int, limit: Int): GetAllLinksResponseDto {
        val links = repository.findAll(page, limit)
        val totalLinks = repository.count()

        return links.map {
            LinkResponseDto(
                mainUrl = it.mainUrl,
                shortenedUrl = it.shortenedUrl
            )
        }.let { linkDtos ->
            GetAllLinksResponseDto(
                data = linkDtos,
                pagination = PaginationResponseDto(
                    total = totalLinks,
                    page = page,
                    limit = limit
                )
            )
        }
    }
}
