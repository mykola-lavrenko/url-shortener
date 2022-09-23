package com.mlavrenko.urlshortener.repository

import com.mlavrenko.urlshortener.domain.ShortenedUrl
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface ShortenedUrlRepository : MongoRepository<ShortenedUrl, String> {
    fun findByShortenedUrl(originalUrl: String): ShortenedUrl?
}