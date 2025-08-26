package com.example.kuba_assignment.cache

import com.example.kuba_assignment.domain.Journey
import com.example.kuba_assignment.domain.JourneyId
import com.example.kuba_assignment.domain.UserId
import exceptions.NotFoundException
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class InMemoryJourneyCacheTest {

    private lateinit var cache: InMemoryJourneyCache
    private val userId = UserId(1L)
    private val nonExistentJourneyId: JourneyId = JourneyId(2L)
    private val nonExistentUserId: UserId = UserId(2L)

    @BeforeEach
    fun setup() {
        cache = InMemoryJourneyCache()
    }

    @Test
    fun `add journey and fetch`() = runTest {
        val journey = Journey(JourneyId(1L), userId)
        cache.addJourney(journey)

        val fetchedJourney = cache.getJourneyById(journey.id)
        assertNotNull(fetchedJourney)
        assertEquals(journey.id, fetchedJourney.id)
        assertEquals(journey.userId, fetchedJourney.userId)

        val userJourneys = cache.getJourneysForUser(userId)
        assertEquals(1, userJourneys.size)
        assertTrue { userJourneys.contains(journey) }
    }


    @Test
    fun `fetching journey returns null if journey does not exist`() = runTest {
        val journey = cache.getJourneyById(nonExistentJourneyId)
        assertNull(journey)
    }

    @Test
    fun `fetching journeys for user returns empty set if user has no journeys`() = runTest {
        val journeys = cache.getJourneysForUser(nonExistentUserId)
        assertTrue(journeys.isEmpty())
    }


    @Test
    fun `add journey and delete`() = runTest {
        val journey = Journey(JourneyId(1L), userId)
        cache.addJourney(journey)

        cache.deleteJourney(journey.id)

        assertNull(cache.getJourneyById(journey.id))
        assertTrue { cache.getJourneysForUser(userId).isEmpty() }
    }

    @Test
    fun `deleting a non-existing journey throws exception`() = runTest {
        val message = assertThrows<NotFoundException> {
            cache.deleteJourney(JourneyId(1L))
        }.message

        assertEquals("There exists no journey with the provided id", message)
    }

}