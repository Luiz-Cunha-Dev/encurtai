package com.encurtador.short_link_manager_service.service

import com.encurtador.short_link_manager_service.client.ShortLinkKeyServiceClient
import com.encurtador.short_link_manager_service.dto.ShortenLinkResponseDto
import com.encurtador.short_link_manager_service.model.ShortenedLink
import com.encurtador.short_link_manager_service.repository.ShortLinkManagerRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class ShortenLinkService(
    private val repository: ShortLinkManagerRepository,
    @Value("\${encurta-ai.kong.url}")
    private val kongUrl: String,
    private val client: ShortLinkKeyServiceClient
) {
    fun execute(mainUrl: String): ShortenLinkResponseDto {
        if (!isUrlValid(mainUrl)) {
            throw IllegalArgumentException("Invalid URL format")
        }

        val keyResponse = client.getUniqueTokenId()
        val shortenedUrl = generateShortenedUrl(keyResponse.uniqueTokenId)
        val shortenedLink = ShortenedLink(
            mainUrl = mainUrl,
            shortenedUrl = shortenedUrl
        )
        repository.save(shortenedLink)

        return ShortenLinkResponseDto(shortenedUrl)
    }

    private fun isUrlValid(url: String): Boolean {
        val urlRegex = "^(https?://)[\\w\\-]+(\\.[\\w\\-]+)+[/#?]?.*$".toRegex()
        return url.matches(urlRegex)
    }

    private fun generateShortenedUrl(shortUrlKey: String): String {
        return kongUrl.plus("/rs/$shortUrlKey")
    }
}
