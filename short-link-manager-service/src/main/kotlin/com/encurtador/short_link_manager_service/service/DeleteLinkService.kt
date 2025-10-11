package com.encurtador.short_link_manager_service.service

import com.encurtador.short_link_manager_service.repository.ShortLinkManagerRepository
import org.springframework.stereotype.Service

@Service
class DeleteLinkService(
    private val repository: ShortLinkManagerRepository
) {
    fun execute(key: String) {
        if (repository.delete(key) == null) {
            throw NoSuchElementException("Link not found")
        }
    }
}
