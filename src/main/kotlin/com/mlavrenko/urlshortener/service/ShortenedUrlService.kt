package com.mlavrenko.urlshortener.service

import com.google.common.hash.Hashing
import com.mlavrenko.urlshortener.domain.ShortenedUrl
import com.mlavrenko.urlshortener.dto.ShortenedUrlDTO
import com.mlavrenko.urlshortener.repository.ShortenedUrlRepository
import org.springframework.stereotype.Service
import java.net.URL
import javax.annotation.Nonnull
import javax.persistence.EntityNotFoundException

@Service
class ShortenedUrlService(private val repository: ShortenedUrlRepository) {

    @Nonnull
    fun shortenUrl(@Nonnull url: URL): ShortenedUrlDTO {
        val originalUrl = url.toString()
        val shortenedUrl = ShortenedUrl(shortenedUrl = encode(originalUrl), originalUrl = originalUrl)
        return toDTO(repository.save(shortenedUrl))
    }

    @Nonnull
    private fun encode(@Nonnull value: String): String {
        return Hashing.murmur3_32_fixed().hashUnencodedChars(value).toString()
    }


    @Nonnull
    fun findByShortenedUrl(@Nonnull shortenedUrl: String): ShortenedUrlDTO {
        val url = repository.findByShortenedUrl(shortenedUrl)
            ?: throw EntityNotFoundException("There is no url found by shortcut=$shortenedUrl")
        return toDTO(url)
    }

    @Nonnull
    private fun toDTO(@Nonnull shortenedUrl: ShortenedUrl): ShortenedUrlDTO {
        return ShortenedUrlDTO(URL(shortenedUrl.originalUrl), shortenedUrl.shortenedUrl)
    }
}