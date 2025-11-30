package com.encurtador.short_link_manager_service.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "shortened_links")
data class ShortenedLink(
    @Id val id: String,
    val mainUrl: String,
    val shortenedUrl: String
)