package com.encurtador.short_link_manager_service.repository

import com.encurtador.short_link_manager_service.model.ShortenedLink

interface ShortLinkManagerRepository {
    fun save(shortenedLink: ShortenedLink): ShortenedLink
    fun findAll(page: Int, limit: Int): List<ShortenedLink>
    fun count(): Long
    fun get(key: String): ShortenedLink?
    fun delete(key: String): ShortenedLink?
}
