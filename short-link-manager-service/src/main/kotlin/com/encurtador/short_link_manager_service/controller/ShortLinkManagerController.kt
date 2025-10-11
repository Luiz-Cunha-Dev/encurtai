package com.encurtador.short_link_manager_service.controller

import com.encurtador.short_link_manager_service.config.GlobalExceptionHandler
import com.encurtador.short_link_manager_service.dto.GetAllLinksResponseDto
import com.encurtador.short_link_manager_service.dto.LinkResponseDto
import com.encurtador.short_link_manager_service.dto.ShortenLinkRequestDto
import com.encurtador.short_link_manager_service.dto.ShortenLinkResponseDto
import com.encurtador.short_link_manager_service.service.DeleteLinkService
import com.encurtador.short_link_manager_service.service.GetAllLinksService
import com.encurtador.short_link_manager_service.service.GetLinkService
import com.encurtador.short_link_manager_service.service.ShortenLinkService
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/")
class ShortLinkManagerController (
    private val shortenLinkService: ShortenLinkService,
    private val getAllLinksService: GetAllLinksService,
    private val getLinkService: GetLinkService,
    private val deleteLinkService: DeleteLinkService
) {
    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun shortenLink(
        @Valid request: ShortenLinkRequestDto
    ): ShortenLinkResponseDto {
        log.info("Received POST request to shorten URL, request: $request")
        return shortenLinkService.execute(request.mainUrl).also {
            log.info("Successfully shortened URL, response: $it")
        }
    }

    @GetMapping
    fun getAllLinks(
        @RequestParam(required = false) page: Int = 0,
        @RequestParam(required = false) limit: Int = 10
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
