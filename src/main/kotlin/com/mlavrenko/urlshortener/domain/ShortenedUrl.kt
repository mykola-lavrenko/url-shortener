package com.mlavrenko.urlshortener.domain;

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.hibernate.validator.constraints.URL
import javax.validation.constraints.NotBlank

@Document
data class ShortenedUrl(
    @Id
    var id: String? = null,
    @get: NotBlank
    val shortenedUrl: String,
    @get: URL
    val originalUrl: String
)
