package com.mlavrenko.urlshortener.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.mlavrenko.urlshortener.dto.ShortenedUrlDTO
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.NullAndEmptySource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import java.net.URL

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("integration-test")
internal class UrlShortenerControllerTest {
    private val validUrl = URL("https://lichess.org/")
    private val expectedShortenedUrl = "265d8502"

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun `should save shortened url when valid url provided`() {
        mockMvc.post("/shorten") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(validUrl)
        }.andDo { print() }
            .andExpect {
                status { isCreated() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    json(objectMapper.writeValueAsString(ShortenedUrlDTO(validUrl, expectedShortenedUrl)))
                }
            }
    }

    @ParameterizedTest
    @NullAndEmptySource
    @CsvSource(value = ["invalid_url", "ftp://server::8080", "htttp://google"])
    fun `should return BAD REQUEST status when url is invalid`(invalidUrl: String?) {
        mockMvc.post("/shorten") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(invalidUrl)
        }.andDo { print() }
            .andExpect {
                status { isBadRequest() }
            }
    }

    @Test
    fun `should return NOT FOUND status when the url does not exist`() {
        val shortenedUrl = "does_not_exist"

        mockMvc.get("/$shortenedUrl")
            .andDo { print() }
            .andExpect { status { isNotFound() } }
    }

    @Test
    fun `should return OK and original url by existing shortcut`() {
        mockMvc.post("/shorten") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(validUrl)
        }

        mockMvc.get("/$expectedShortenedUrl")
            .andDo { print() }
            .andExpect {
                status { isOk() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    json(objectMapper.writeValueAsString(ShortenedUrlDTO(validUrl, expectedShortenedUrl)))
                }
            }
    }
}