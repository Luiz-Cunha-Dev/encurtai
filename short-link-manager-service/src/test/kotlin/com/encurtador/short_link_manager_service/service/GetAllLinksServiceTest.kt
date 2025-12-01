package com.encurtador.short_link_manager_service.service

import com.encurtador.short_link_manager_service.dto.GetAllLinksResponseDto
import com.encurtador.short_link_manager_service.dto.LinkResponseDto
import com.encurtador.short_link_manager_service.dto.PaginationResponseDto
import com.encurtador.short_link_manager_service.model.ShortenedLink
import com.encurtador.short_link_manager_service.repository.ShortLinkManagerMongoRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import kotlin.test.assertEquals

internal class GetAllLinksServiceTest {
    private val repository = mockk<ShortLinkManagerMongoRepository>()
    private val service = GetAllLinksService(repository)

    @Test
    fun `should get all links successfully`() {
        val page = 0
        val limit = 10
        val pageRequest = PageRequest.of(page, limit)
        val total = 2L

        val linksPage = PageImpl(
            listOf(
                ShortenedLink(
                    id = "abc123",
                    mainUrl = "https://example.com",
                    shortenedUrl = "https://mock/abc123"
                ),
                ShortenedLink(
                    id = "def456",
                    mainUrl = "https://example.org",
                    shortenedUrl = "https://mock/def456"
                )
            ),
            pageRequest,
            total
        )

        every {
            repository.findAll(pageRequest)
        } returns linksPage

        val expectedLinkResponse = GetAllLinksResponseDto(
            data = listOf(
                LinkResponseDto(
                    mainUrl = "https://example.com",
                    shortenedUrl = "https://mock/abc123"
                ),
                LinkResponseDto(
                    mainUrl = "https://example.org",
                    shortenedUrl = "https://mock/def456"
                )
            ),
            pagination = PaginationResponseDto(
                total = total,
                page = page,
                limit = limit
            )
        )

        val actualLinkResponse = service.execute(page, limit)

        assertEquals(expectedLinkResponse, actualLinkResponse)

        verify(exactly = 1) { repository.findAll(pageRequest) }
    }
}