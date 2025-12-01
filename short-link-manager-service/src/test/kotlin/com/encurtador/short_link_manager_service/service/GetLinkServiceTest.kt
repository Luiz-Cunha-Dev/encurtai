package com.encurtador.short_link_manager_service.service

import com.encurtador.short_link_manager_service.dto.LinkResponseDto
import com.encurtador.short_link_manager_service.model.ShortenedLink
import com.encurtador.short_link_manager_service.repository.ShortLinkManagerMongoRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class GetLinkServiceTest {
    private val repository = mockk<ShortLinkManagerMongoRepository>()
    private val service = GetLinkService(repository)

    @Test
    fun `should get link successfully`() {
        val shortenedLinkMock = ShortenedLink(
            id = "abc123",
            mainUrl = "https://example.com",
            shortenedUrl = "https://mock/abc123"
        )

        every {
            repository.findById("abc123")
        } returns Optional.of(shortenedLinkMock)

        val expectedLinkResponse = LinkResponseDto(
            mainUrl = shortenedLinkMock.mainUrl,
            shortenedUrl = shortenedLinkMock.shortenedUrl
        )

        val actualLinkResponse = service.execute("abc123")

        assertEquals(expectedLinkResponse, actualLinkResponse)

        verify(exactly = 1) { repository.findById("abc123") }
    }

    @Test
    fun `should throw no such element exception when link not found`() {
        every {
            repository.findById("nonexistent")
        } returns Optional.empty()

        val exception = assertFailsWith<NoSuchElementException> {
            service.execute("nonexistent")
        }

        assertEquals("Link not found", exception.message)

        verify(exactly = 1) { repository.findById("nonexistent") }
    }
}
