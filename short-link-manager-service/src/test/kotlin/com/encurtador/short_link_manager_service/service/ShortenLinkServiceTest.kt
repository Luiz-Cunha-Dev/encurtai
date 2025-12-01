package com.encurtador.short_link_manager_service.service

import com.encurtador.short_link_manager_service.client.ShortLinkKeyServiceClient
import com.encurtador.short_link_manager_service.dto.ShortLinkKeyResponseDto
import com.encurtador.short_link_manager_service.model.ShortenedLink
import com.encurtador.short_link_manager_service.repository.ShortLinkManagerMongoRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class ShortenLinkServiceTest {
    private val shortLinkManagerRepository = mockk<ShortLinkManagerMongoRepository>()
    private val kongUrl = "http://localhost:8000"
    private val shortLinkKeyServiceClient = mockk<ShortLinkKeyServiceClient>()
    private val shortenLinkService = ShortenLinkService(shortLinkManagerRepository, kongUrl, shortLinkKeyServiceClient)

    @Test
    fun `should shorten a valid URL`() {
        val uniqueTokenId = "abc123"
        val url = "https://www.example.com"
        val shortenedLink = ShortenedLink(
            id = uniqueTokenId,
            mainUrl = url,
            shortenedUrl = "$kongUrl/rs/$uniqueTokenId"
        )

        every {
            shortLinkKeyServiceClient.getUniqueTokenId()
        } returns ShortLinkKeyResponseDto(uniqueTokenId)

        every {
            shortLinkManagerRepository.save(shortenedLink)
        } returns shortenedLink

        val shortenLinkResponseDto = shortenLinkService.execute(url)

        assertNotNull(shortenLinkResponseDto)
        assertEquals(shortenedLink.shortenedUrl, shortenLinkResponseDto.shortenedUrl)

        verify(exactly = 1) { shortLinkKeyServiceClient.getUniqueTokenId() }
        verify(exactly = 1) { shortLinkManagerRepository.save(shortenedLink) }
    }

    @Test
    fun `shoud throw illegal argument exception when url is invalid`() {
        val invalidUrl = "htp:/invalid-url"

        val exception = assertFailsWith<IllegalArgumentException> {
            shortenLinkService.execute(invalidUrl)
        }

        assertEquals("Invalid URL format", exception.message)

        verify(exactly = 0) { shortLinkKeyServiceClient.getUniqueTokenId() }
        verify(exactly = 0) { shortLinkManagerRepository.save(any()) }
    }
}
