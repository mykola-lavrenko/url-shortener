package com.mlavrenko.urlshortener.controller

import com.mlavrenko.urlshortener.dto.ShortenedUrlDTO
import com.mlavrenko.urlshortener.service.ShortenedUrlService
import org.springframework.cache.annotation.Cacheable
import org.springframework.core.convert.ConversionException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.net.URL
import javax.persistence.EntityNotFoundException
import javax.validation.ValidationException

@RestController
class UrlShortenerController(private val shortenedUrlService: ShortenedUrlService) {

    @ExceptionHandler(EntityNotFoundException::class)
    fun handleNotFound(e: EntityNotFoundException): ResponseEntity<String> =
        ResponseEntity(e.message, HttpStatus.NOT_FOUND)

    @ExceptionHandler(value = [
        IllegalArgumentException::class,
        ConversionException::class,
        ValidationException::class,
    ])
    fun handleBadRequest(e: Exception): ResponseEntity<String> =
        ResponseEntity(e.message, HttpStatus.BAD_REQUEST)

    @PostMapping("/shorten")
    @ResponseStatus(HttpStatus.CREATED)
    fun shortenUrl(@Validated @RequestBody url: URL): ShortenedUrlDTO {
        return shortenedUrlService.shortenUrl(url)
    }

    @Cacheable("shortened-urls")
    @GetMapping("/{shortenedUrl}")
    fun redirect(@PathVariable shortenedUrl: String): ShortenedUrlDTO {
        return shortenedUrlService.findByShortenedUrl(shortenedUrl)
    }
}