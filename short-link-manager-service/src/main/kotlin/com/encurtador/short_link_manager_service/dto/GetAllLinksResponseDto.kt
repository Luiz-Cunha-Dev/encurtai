package com.encurtador.short_link_manager_service.dto

data class GetAllLinksResponseDto(
    val data: List<LinkResponseDto>,
    val pagination: PaginationResponseDto
)

data class LinkResponseDto(
    val mainUrl: String,
    val shortenedUrl: String
)

data class PaginationResponseDto(
    val total: Long,
    val page: Int,
    val limit: Int
)
