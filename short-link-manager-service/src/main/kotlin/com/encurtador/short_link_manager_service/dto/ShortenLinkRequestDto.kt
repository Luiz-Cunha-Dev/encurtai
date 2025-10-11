package com.encurtador.short_link_manager_service.dto

import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull

data class ShortenLinkRequestDto(
    @NotNull
    @NotEmpty
    val mainUrl: String
)
