package com.encurtador.short_link_manager_service.dto

data class ErrorResponseDto(
    val timestamp: Long,
    val status: Int,
    val error: String,
    val message: String?,
)
