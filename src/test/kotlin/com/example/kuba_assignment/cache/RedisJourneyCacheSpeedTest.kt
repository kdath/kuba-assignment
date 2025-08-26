package com.example.kuba_assignment.cache

import com.example.kuba_assignment.domain.Journey
import com.example.kuba_assignment.domain.JourneyId
import com.example.kuba_assignment.domain.UserId
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import kotlin.random.Random
import kotlin.system.measureTimeMillis

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RedisJourneyCacheSpeedTest {

    companion object {
        @Container
        val redisContainer = GenericContainer("redis:7-alpine").apply {
            withExposedPorts(6379)
        }
    }

    private lateinit var cache: RedisJourneyCache
    private lateinit var reactiveTemplate: ReactiveRedisTemplate<String, Journey>

    private val totalJourneys = 10000L
    private val totalUsers = 1000L

    @BeforeAll
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
            for (i in 1..totalJourneys) {
                val userId = UserId((i % totalUsers) + 1)
                val journeyId = JourneyId(i)
                cache.addJourney(Journey(journeyId, userId))
            }
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
        println("Fetched all journeys for $totalUsers users in $duration ms")
    }

}