package com.example.kuba_assignment.cache

import com.example.kuba_assignment.domain.Journey
import com.example.kuba_assignment.domain.JourneyId
import com.example.kuba_assignment.domain.UserId

interface JourneyCache {
    suspend fun getJourneyById(id: JourneyId): Journey?
    suspend fun getJourneysForUser(userId: UserId): Set<Journey>
    suspend fun addJourney(journey: Journey)
    suspend fun deleteJourney(id: JourneyId)
    fun clear()
}