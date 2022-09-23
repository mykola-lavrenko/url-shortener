package com.mlavrenko.urlshortener.dto

import java.io.Serializable
import java.net.URL

data class ShortenedUrlDTO(val originalUrl: URL, val shortenedUrl: String): Serializable