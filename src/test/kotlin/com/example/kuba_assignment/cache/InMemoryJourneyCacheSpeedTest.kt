package com.example.kuba_assignment.cache

import com.example.kuba_assignment.domain.Journey
import com.example.kuba_assignment.domain.JourneyId
import com.example.kuba_assignment.domain.UserId
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import kotlin.random.Random
import kotlin.system.measureTimeMillis

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class InMemoryJourneyCacheSpeedTest {

    private lateinit var cache: InMemoryJourneyCache
    private val totalJourneys = 1000000L
    private val totalUsers = 1000L

    @BeforeAll
    fun setupOnce() = runBlocking {
        cache = InMemoryJourneyCache()

        for (i in 1..totalJourneys) {
            val userId = UserId((i % totalUsers) + 1)
            val journeyId = JourneyId(i)
            cache.addJourney(Journey(journeyId, userId))
        }
    }

    @Test
    fun `measure single journey retrieval speed`() = runBlocking {
        val numberOfFetches = 100000
        val duration = measureTimeMillis {
            repeat(numberOfFetches) {
                val randomId = JourneyId(Random.nextLong(1, totalJourneys + 1))
                cache.getJourneyById(randomId)
            }
        }
        println("Fetched $numberOfFetches journeys in $duration ms")
    }

    @Test
    fun `measure all journeys for user retrieval speed`() = runBlocking {
        val numberOfFetches = 10000
        val duration = measureTimeMillis {
            repeat(numberOfFetches) {
                val randomUser = UserId(Random.nextLong(1, totalUsers + 1))
                cache.getJourneysForUser(randomUser)
            }
        }
        println("Fetched all $numberOfFetches for $totalUsers users in $duration ms")
    }
}
