package com.example.kuba_assignment.cache

import com.example.kuba_assignment.domain.Journey
import com.example.kuba_assignment.domain.JourneyId
import com.example.kuba_assignment.domain.UserId
import exceptions.NotFoundException
import java.util.concurrent.ConcurrentHashMap

class InMemoryJourneyCache : JourneyCache {

    private val journeyIdMap = ConcurrentHashMap<JourneyId, Journey>()
    private val userIdMap = ConcurrentHashMap<UserId, MutableSet<Journey>>()

    override suspend fun getJourneyById(id: JourneyId): Journey? {
        return journeyIdMap[id]
    }

    override suspend fun getJourneysForUser(userId: UserId): Set<Journey> {
        return userIdMap[userId]?.toSet() ?: ConcurrentHashMap.newKeySet()
    }

    override suspend fun addJourney(journey: Journey) {
        journeyIdMap[journey.id] = journey
        userIdMap.computeIfAbsent(journey.userId) { ConcurrentHashMap.newKeySet() }.add(journey)
    }

    override suspend fun deleteJourney(id: JourneyId) {
        val journey = journeyIdMap[id] ?: throw NotFoundException("There exists no journey with the provided id")
        userIdMap[journey.userId]?.remove(journey)
        journeyIdMap.remove(id)
    }

    override fun clear() {
        journeyIdMap.clear()
        userIdMap.clear()
    }
}