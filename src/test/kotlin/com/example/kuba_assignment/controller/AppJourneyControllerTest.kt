package com.example.kuba_assignment.controller

import com.example.kuba_assignment.cache.JourneyCache
import com.example.kuba_assignment.domain.Journey
import com.example.kuba_assignment.domain.JourneyId
import com.example.kuba_assignment.domain.UserId
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.context.annotation.Import
import org.springframework.test.web.reactive.server.WebTestClient

@WebFluxTest(AppJourneyController::class)
@Import(TestConfig::class)
class AppJourneyControllerTest {

    @Autowired
    lateinit var webClient: WebTestClient

    @Autowired
    lateinit var cache: JourneyCache

    private val userId = UserId(1L)
    private val journeyId = JourneyId(42L)
    private val journey = Journey(journeyId, userId)


    @BeforeEach
    fun setup() {
        runBlocking {
            cache.addJourney(journey)
        }
    }

    @AfterEach
    fun tearDown() {
        cache.clear()
    }

    @Test
    fun `GET journey returns journey`() {
        webClient.get()
            .uri("/app/journey/${journeyId.id}")
            .header("api-user-id", userId.id.toString())
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.id").isEqualTo(journeyId.id)
    }

    @Test
    fun `GET journey returns 404 if journey doesn't exist`(): Unit = runBlocking {
        webClient.get()
            .uri("/app/journey/43")
            .header("api-user-id", userId.id.toString())
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun `DELETE journey deletes the journey`(): Unit = runBlocking {
        webClient.delete()
            .uri("/app/journey/${journeyId.id}")
            .header("api-user-id", userId.id.toString())
            .exchange()
            .expectStatus().isNoContent
    }

    @Test
    fun `DELETE journey return 404 if journey doesn't exist`(): Unit = runBlocking {
        webClient.delete()
            .uri("/app/journey/43")
            .header("api-user-id", userId.id.toString())
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun `POST journey creates a journey`(): Unit = runBlocking {
        webClient.post()
            .uri("/app/journey")
            .header("api-user-id", userId.id.toString())
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .json("{id:1}")
    }
}