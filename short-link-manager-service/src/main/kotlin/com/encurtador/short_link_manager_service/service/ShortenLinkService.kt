package com.encurtador.short_link_manager_service.service

import com.encurtador.short_link_manager_service.dto.ShortenLinkResponseDto
import com.encurtador.short_link_manager_service.model.ShortenedLink
import com.encurtador.short_link_manager_service.repository.ShortLinkManagerRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class ShortenLinkService(
    private val repository: ShortLinkManagerRepository,
    @Value("\${encurta-ai.kong.url}")
    private val kongUrl: String
) {
    fun execute(mainUrl: String): ShortenLinkResponseDto {
        if (!isUrlValid(mainUrl)) {
            throw IllegalArgumentException("Invalid URL format")
        }

        val urlKey = mockShortUrlKey()
        val shortenedUrl = generateShortenedUrl(urlKey)
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

    private fun mockShortUrlKey(): String {
        return List(6) {
            (('a'..'z') + ('A'..'Z') + ('0'..'9')).random()
        }.joinToString("")
    }

    private fun generateShortenedUrl(shortUrlKey: String): String {
        return kongUrl.plus("/rs/$shortUrlKey")
    }
}
