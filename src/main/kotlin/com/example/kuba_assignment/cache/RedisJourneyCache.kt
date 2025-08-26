package com.example.kuba_assignment.cache

import com.example.kuba_assignment.domain.Journey
import com.example.kuba_assignment.domain.JourneyId
import com.example.kuba_assignment.domain.UserId
import com.example.kuba_assignment.exceptions.NotFoundException
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.data.redis.core.ReactiveRedisTemplate

class RedisJourneyCache(
    private val redis: ReactiveRedisTemplate<String, Journey>
) : JourneyCache {

    private val valueOps = redis.opsForValue()
    private val setOps = redis.opsForSet()

    override suspend fun getJourneyById(id: JourneyId): Journey? {
        return valueOps.get("journey:${id.id}").awaitFirstOrNull()
    }

    override suspend fun getJourneysForUser(userId: UserId): Set<Journey> {
        val journeys: List<Journey> = setOps.members("user:${userId.id}:journeys").collectList().awaitSingle()

        return if (journeys.isEmpty()) {
            emptySet()
        } else {
            journeys.toSet()
        }
    }

    override suspend fun addJourney(journey: Journey) {
        valueOps.set("journey:${journey.id.id}", journey).awaitSingle()
        setOps.add("user:${journey.userId.id}:journeys", journey).awaitSingle()
    }

    override suspend fun deleteJourney(id: JourneyId) {
        val journey = valueOps.get("journey:${id.id}").awaitFirstOrNull()
            ?: throw NotFoundException("There exists no journey with the provided id")

        setOps.remove("user:${journey.userId.id}:journeys", journey).awaitFirstOrNull()

        valueOps.delete("journey:${id.id}").awaitFirstOrNull()
    }

    override fun clear() {
        redis.connectionFactory.reactiveConnection.serverCommands().flushAll()
    }

}