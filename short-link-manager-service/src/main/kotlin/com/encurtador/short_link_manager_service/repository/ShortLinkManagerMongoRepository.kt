package com.encurtador.short_link_manager_service.repository

import com.encurtador.short_link_manager_service.model.ShortenedLink
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface ShortLinkManagerMongoRepository : MongoRepository<ShortenedLink, String>