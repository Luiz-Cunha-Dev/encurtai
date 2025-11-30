package com.encurtador.short_link_manager_service.service

import com.encurtador.short_link_manager_service.repository.ShortLinkManagerMongoRepository
import org.springframework.stereotype.Service

@Service
class DeleteLinkService(
    private val repository: ShortLinkManagerMongoRepository
) {
    fun execute(key: String) {
        val shortenedLink = repository.findById(key).orElseThrow { NoSuchElementException("Link not found") }
        repository.delete(shortenedLink)
    }
}
