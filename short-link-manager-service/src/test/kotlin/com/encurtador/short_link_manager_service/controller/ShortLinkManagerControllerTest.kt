package com.encurtador.short_link_manager_service.controller

import com.encurtador.short_link_manager_service.dto.GetAllLinksResponseDto
import com.encurtador.short_link_manager_service.dto.LinkResponseDto
import com.encurtador.short_link_manager_service.dto.ShortenLinkRequestDto
import com.encurtador.short_link_manager_service.dto.ShortenLinkResponseDto
import com.encurtador.short_link_manager_service.model.ShortenedLink
import com.encurtador.short_link_manager_service.repository.ShortLinkManagerMongoRepository
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.justRun
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpMethod
import org.springframework.test.context.ActiveProfiles
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
internal class ShortLinkManagerControllerTest {
    @Autowired private lateinit var restTemplate: TestRestTemplate
    @MockkBean private lateinit var shortLinkMongoRepository: ShortLinkManagerMongoRepository

    @Test
    fun `should shorten link successfully`() {
        val shortenedLinkStub = ShortenedLink(
            id = "abc123",
            mainUrl = "https://www.example.com/some/long/path",
            shortenedUrl = "http://localhost:8000/rs/abc123"
        )

        val shortenLinkRequest = ShortenLinkRequestDto(shortenedLinkStub.mainUrl)

        mockShortLinkKeyGetUniqueTokenId()

        every {
            shortLinkMongoRepository.save(shortenedLinkStub)
        } returns shortenedLinkStub

        val shortenedLinkResponse = restTemplate.postForEntity(
            "/",
            shortenLinkRequest,
            ShortenLinkResponseDto::class.java
        )

        assertEquals(201, shortenedLinkResponse.statusCode.value())
        assertTrue(shortenedLinkResponse.body!!.shortenedUrl.contains("abc123"))
        assertEquals(shortenedLinkStub.shortenedUrl, shortenedLinkResponse.body!!.shortenedUrl)
    }

    @Test
    fun `should get link successfully`() {
        val shortenedLinkStub = ShortenedLink(
            id = "abc123",
            mainUrl = "https://www.example.com/some/long/path",
            shortenedUrl = "http://localhost:8000/rs/abc123"
        )

        every {
            shortLinkMongoRepository.findById("abc123")
        } returns Optional.of(shortenedLinkStub)

        val shortenedLinkResponse = restTemplate.getForEntity(
            "/abc123",
            LinkResponseDto::class.java
        )

        assertTrue(shortenedLinkResponse.statusCode.is2xxSuccessful)
        assertEquals("https://www.example.com/some/long/path", shortenedLinkResponse.body!!.mainUrl)
        assertEquals("http://localhost:8000/rs/abc123", shortenedLinkResponse.body!!.shortenedUrl)
    }

    @Test
    fun `should get all links successfully`() {
        val shortenedLinkStub1 = ShortenedLink(
            id = "abc123",
            mainUrl = "https://www.example.com/some/long/path",
            shortenedUrl = "http://localhost:8000/rs/abc123"
        )
        val shortenedLinkStub2 = ShortenedLink(
            id = "def456",
            mainUrl = "https://www.anotherexample.com/different/path",
            shortenedUrl = "http://localhost:8000/rs/def456"
        )

        every {
            shortLinkMongoRepository.findAll(PageRequest.of(0, 10))
        } returns PageImpl(
            listOf(shortenedLinkStub1, shortenedLinkStub2),
            PageRequest.of(0, 10),
            2L
        )

        val shortenedLinksResponse = restTemplate.getForEntity(
            "/",
            GetAllLinksResponseDto::class.java
        )

        assertTrue(shortenedLinksResponse.statusCode.is2xxSuccessful)
        val body = shortenedLinksResponse.body!!
        assertEquals(2, body.pagination.total)
        assertEquals(0, body.pagination.page)
        assertEquals(10, body.pagination.limit)
        assertEquals(2, body.data.size)
        assertEquals("https://www.example.com/some/long/path", body.data[0].mainUrl)
        assertEquals("http://localhost:8000/rs/abc123", body.data[0].shortenedUrl)
        assertEquals("https://www.anotherexample.com/different/path", body.data[1].mainUrl)
        assertEquals("http://localhost:8000/rs/def456", body.data[1].shortenedUrl)
    }

    @Test
    fun `should delete link successfully`() {
        val shortenedLinkStub = ShortenedLink(
            id = "abc123",
            mainUrl = "https://www.example.com/some/long/path",
            shortenedUrl = "http://localhost:8000/rs/abc123"
        )

        every {
            shortLinkMongoRepository.findById("abc123")
        } returns Optional.of(shortenedLinkStub)

        justRun {
            shortLinkMongoRepository.delete(shortenedLinkStub)
        }

        val response = restTemplate.exchange(
            "/abc123",
            HttpMethod.DELETE,
            null,
            Void::class.java
        )

        assertEquals(204, response.statusCode.value())
    }
    
    private fun mockShortLinkKeyGetUniqueTokenId() {
        val responseBody = """
            {
                "uniqueTokenId": "abc123"
            }
        """.trimIndent()

        stubFor(get(urlEqualTo("/"))
            .willReturn(
                aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withBody(responseBody)
                    .withStatus(200)
            )
        )
    }
}
