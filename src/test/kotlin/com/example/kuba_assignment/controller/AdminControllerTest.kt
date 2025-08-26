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

@WebFluxTest(AdminController::class)
@Import(TestConfig::class)
class AdminControllerTest {

    @Autowired
    lateinit var webClient: WebTestClient

    @Autowired
    lateinit var cache: JourneyCache

    private val userId = UserId(1L)
    private val differentUserId = UserId(2L)
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
            .uri("/admin/journey/${journeyId.id}")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.id").isEqualTo(journeyId.id)
    }

    @Test
    fun `GET journey returns 404 if journey doesn't exist`() {
        webClient.get()
            .uri("/admin/journey/43")
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun `GET all journeys for user returns journeys`() {
        webClient.get()
            .uri("/admin/user/${userId.id}/journeys")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .json("[{id:42}]")
    }

    @Test
    fun `GET all journeys for user without journeys return empty list`() {
        webClient.get()
            .uri("/admin/user/${differentUserId.id}/journeys")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .json("[]")
    }
}