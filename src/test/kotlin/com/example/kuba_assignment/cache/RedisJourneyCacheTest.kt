package com.example.kuba_assignment.cache

import com.example.kuba_assignment.domain.Journey
import com.example.kuba_assignment.domain.JourneyId
import com.example.kuba_assignment.domain.UserId
import com.example.kuba_assignment.exceptions.NotFoundException
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.*
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
class RedisJourneyCacheTest {

    companion object {
        @Container
        val redisContainer = GenericContainer("redis:7-alpine").apply {
            withExposedPorts(6379)
        }
    }

    private val nonExistentJourneyId: JourneyId = JourneyId(2L)
    private val nonExistentUserId: UserId = UserId(2L)
    private lateinit var cache: RedisJourneyCache
    private lateinit var reactiveTemplate: ReactiveRedisTemplate<String, Journey>
    private val userId = UserId(1L)

    @BeforeEach
    fun setup() {
        val host = redisContainer.host
        val port = redisContainer.getMappedPort(6379)
        val factory = LettuceConnectionFactory(host, port)
        factory.afterPropertiesSet()

        val serializer = Jackson2JsonRedisSerializer(Journey::class.java)
        val stringSerializer = StringRedisSerializer()

        val serializationContext = RedisSerializationContext
            .newSerializationContext<String, Journey>(stringSerializer)
            .value(serializer)
            .build()

        reactiveTemplate = ReactiveRedisTemplate(factory, serializationContext)
        cache = RedisJourneyCache(reactiveTemplate)

        runBlocking {
            reactiveTemplate.execute { connection ->
                connection.serverCommands().flushDb()
            }.awaitSingle()
        }
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