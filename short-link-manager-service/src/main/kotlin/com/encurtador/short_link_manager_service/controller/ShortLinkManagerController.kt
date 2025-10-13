package com.encurtador.short_link_manager_service.controller

import com.encurtador.short_link_manager_service.dto.GetAllLinksResponseDto
import com.encurtador.short_link_manager_service.dto.LinkResponseDto
import com.encurtador.short_link_manager_service.dto.ShortenLinkRequestDto
import com.encurtador.short_link_manager_service.dto.ShortenLinkResponseDto
import com.encurtador.short_link_manager_service.service.DeleteLinkService
import com.encurtador.short_link_manager_service.service.GetAllLinksService
import com.encurtador.short_link_manager_service.service.GetLinkService
import com.encurtador.short_link_manager_service.service.ShortenLinkService
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/")
class ShortLinkManagerController (
    private val shortenLinkService: ShortenLinkService,
    private val getAllLinksService: GetAllLinksService,
    private val getLinkService: GetLinkService,
    private val deleteLinkService: DeleteLinkService
) {
    private val log = LoggerFactory.getLogger(ShortLinkManagerController::class.java)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun shortenLink(
        @Valid @RequestBody request: ShortenLinkRequestDto
    ): ShortenLinkResponseDto {
        log.info("Received POST request to shorten URL, request: $request")
        return shortenLinkService.execute(request.mainUrl).also {
            log.info("Successfully shortened URL, response: $it")
        }
    }

    @GetMapping
    fun getAllLinks(
        @RequestParam(required = false, defaultValue = "1") @Min(1) page: Int = 1,
        @RequestParam(required = false, defaultValue = "10") limit: Int = 10
    ): GetAllLinksResponseDto {
        log.info("Received GET request to fetch all links, page: $page, limit: $limit")
        return getAllLinksService.execute(page, limit).also {
            log.info("Successfully fetched all links, response: $it")
        }
    }

    @GetMapping("/{key}")
    fun getLink(@PathVariable key: String): LinkResponseDto {
        log.info("Received GET request to fetch link, key: $key")
        return getLinkService.execute(key).also {
            log.info("Successfully fetched link, response: $it")
        }
    }

    @DeleteMapping("/{key}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteLink(@PathVariable key: String) {
        log.info("Received DELETE request to delete link, key: $key")
        deleteLinkService.execute(key)
        log.info("Successfully deleted link, key: $key")
    }
}
