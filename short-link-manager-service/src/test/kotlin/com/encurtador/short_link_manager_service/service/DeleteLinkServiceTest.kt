package com.encurtador.short_link_manager_service.service

import com.encurtador.short_link_manager_service.model.ShortenedLink
import com.encurtador.short_link_manager_service.repository.ShortLinkManagerMongoRepository
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class DeleteLinkServiceTest {
    private val repository = mockk<ShortLinkManagerMongoRepository>()
    private val service = DeleteLinkService(repository)

    @Test
    fun `should delete link successfully`() {
        val shortenedLinkMock = mockk<ShortenedLink>()

        every {
            repository.findById("abc123")
        } returns Optional.of(shortenedLinkMock)

        justRun {
            repository.delete(shortenedLinkMock)
        }

        service.execute("abc123")

        verify(exactly = 1) { repository.findById("abc123") }
        verify(exactly = 1) { repository.delete(shortenedLinkMock) }
    }

    @Test
    fun `should throw no such element exception when link not found`() {
        every {
            repository.findById("nonexistent")
        } returns Optional.empty()


        val exception = assertFailsWith<NoSuchElementException> {
            service.execute("nonexistent")
        }

        assertEquals("Link not found", exception.message)

        verify(exactly = 1) { repository.findById("nonexistent") }
        verify(exactly = 0) { repository.delete(any()) }
    }
}