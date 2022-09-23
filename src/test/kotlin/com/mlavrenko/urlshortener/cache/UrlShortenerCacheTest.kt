package com.mlavrenko.urlshortener.cache

import com.fasterxml.jackson.databind.ObjectMapper
import com.mlavrenko.urlshortener.dto.ShortenedUrlDTO
import com.mlavrenko.urlshortener.service.ShortenedUrlService
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.times
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.cache.CacheManager
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import redis.embedded.RedisServer
import java.net.URL


@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles(profiles = ["integration-test", "cache-test"])
internal class UrlShortenerCacheTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var shortenedUrlService: ShortenedUrlService

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var cacheManager: CacheManager

    private val redisServer: RedisServer = RedisServer(6379)

    @BeforeAll
    fun before() {
        redisServer.start()
    }

    @AfterAll
    fun after() {
        redisServer.stop()
    }

    @Test
    fun redirect() {
        val shortenedUrl = "shortenedUrl"
        val originalUrl = URL("https://lichess.org/")
        val shortenedUrlDTO = ShortenedUrlDTO(originalUrl, shortenedUrl)
        given(shortenedUrlService.findByShortenedUrl(shortenedUrl)).willReturn(shortenedUrlDTO)
        mockMvc.get("/$shortenedUrl")
            .andDo { print() }
            .andExpect {
                status { isOk() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    json(objectMapper.writeValueAsString(shortenedUrlDTO))
                }
            }

        mockMvc.get("/$shortenedUrl")
            .andDo { print() }
            .andExpect {
                status { isOk() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    json(objectMapper.writeValueAsString(shortenedUrlDTO))
                }
            }

        assertAll(
            { Mockito.verify(shortenedUrlService, times(1)).findByShortenedUrl(shortenedUrl) },
            { assertEquals(cacheManager.getCache("shortened-urls")?.get(shortenedUrl)?.get(), shortenedUrlDTO) }
        )
    }
}