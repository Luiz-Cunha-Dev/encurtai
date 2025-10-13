package com.encurtador.short_link_manager_service.repository

import com.encurtador.short_link_manager_service.model.ShortenedLink
import org.springframework.stereotype.Component

@Component
class ShortLinkManagerInMemoryRepository : ShortLinkManagerRepository {
    private val storage = mutableMapOf<String, ShortenedLink>()

    override fun save(shortenedLink: ShortenedLink): ShortenedLink {
        storage[shortenedLink.shortenedUrl.substringAfterLast("/")] = shortenedLink
        return shortenedLink
    }

    override fun findAll(page: Int, limit: Int): List<ShortenedLink> {
        val fromIndex = page * limit
        val toIndex = (fromIndex + limit).coerceAtMost(storage.size)
        return if (fromIndex >= storage.size) {
            emptyList()
        } else {
            storage.values.toList().subList(fromIndex, toIndex)
        }
    }

    override fun count(): Long {
        return storage.size.toLong()
    }

    override fun get(key: String): ShortenedLink? {
        return storage[key]
    }

    override fun delete(key: String): ShortenedLink? {
        val removedLink = storage.remove(key)
        return removedLink
    }
}
