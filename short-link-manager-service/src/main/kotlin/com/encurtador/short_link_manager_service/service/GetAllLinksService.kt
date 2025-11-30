package com.encurtador.short_link_manager_service.service

import com.encurtador.short_link_manager_service.dto.GetAllLinksResponseDto
import com.encurtador.short_link_manager_service.dto.LinkResponseDto
import com.encurtador.short_link_manager_service.dto.PaginationResponseDto
import com.encurtador.short_link_manager_service.repository.ShortLinkManagerMongoRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class GetAllLinksService(
    private val repository: ShortLinkManagerMongoRepository
) {
    fun execute(page: Int, limit: Int): GetAllLinksResponseDto {
        val links = repository.findAll(PageRequest.of(page, limit))

        return links.content.map {
            LinkResponseDto(
                mainUrl = it.mainUrl,
                shortenedUrl = it.shortenedUrl
            )
        }.let { linkDtos ->
            GetAllLinksResponseDto(
                data = linkDtos,
                pagination = PaginationResponseDto(
                    total = links.totalElements,
                    page = links.pageable.pageNumber,
                    limit = links.pageable.pageSize
                )
            )
        }
    }
}
