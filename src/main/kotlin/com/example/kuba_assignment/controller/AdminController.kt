package com.example.kuba_assignment.controller

import com.example.kuba_assignment.cache.JourneyCache
import com.example.kuba_assignment.domain.Journey
import com.example.kuba_assignment.domain.JourneyId
import com.example.kuba_assignment.domain.UserId
import com.example.kuba_assignment.exceptions.NotFoundException
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin")
class AdminController(private val cache: JourneyCache) {

    @GetMapping("/user/{user_id}/journeys")
    suspend fun fetchAllJourneysForUser(
        @PathVariable("user_id") userId: Long
    ): Set<Journey> = cache.getJourneysForUser(UserId(userId))

    @GetMapping("/journey/{journey_id}")
    suspend fun fetch(
        @PathVariable("journey_id") journeyId: Long
    ): Journey =
        cache.getJourneyById(JourneyId(journeyId))
            ?: throw NotFoundException("Journey not found")
}