package com.encurtador.short_link_manager_service.config

import com.encurtador.short_link_manager_service.dto.ErrorResponseDto
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleException(ex: Exception): ErrorResponseDto {
        log.error("An unexpected error occurred: ", ex)
        return ErrorResponseDto(
            timestamp = System.currentTimeMillis(),
            status = 500,
            error = "Internal Server Error",
            message = "An unexpected error occurred. Please try again later."
        )
    }

    @ExceptionHandler(IllegalArgumentException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleIllegalArgumentException(ex: IllegalArgumentException): ErrorResponseDto {
        log.warn("Invalid input: ", ex)
        return ErrorResponseDto(
            timestamp = System.currentTimeMillis(),
            status = 400,
            error = "Bad Request",
            message = ex.message
        )
    }

    @ExceptionHandler(NoSuchElementException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNoSuchElementException(ex: NoSuchElementException): ErrorResponseDto {
        log.warn("Resource not found: ", ex)
        return ErrorResponseDto(
            timestamp = System.currentTimeMillis(),
            status = 404,
            error = "Not Found",
            message = ex.message
        )
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleValidationException(ex: MethodArgumentNotValidException): ErrorResponseDto {
        log.warn("Validation failed: ", ex)
        return ErrorResponseDto(
            timestamp = System.currentTimeMillis(),
            status = 400,
            error = "Bad Request",
            message = "Invalid input data"
        )
    }
}