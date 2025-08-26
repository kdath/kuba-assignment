package com.example.kuba_assignment.controller

import com.example.kuba_assignment.util.IdGenerator
import com.example.kuba_assignment.cache.JourneyCache
import com.example.kuba_assignment.domain.Journey
import com.example.kuba_assignment.domain.JourneyId
import com.example.kuba_assignment.domain.UserId
import com.example.kuba_assignment.exceptions.ForbiddenException
import com.example.kuba_assignment.exceptions.NotFoundException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/app")
class AppJourneyController(private val cache: JourneyCache) {

    @PostMapping("/journey")
    suspend fun create(
        @RequestHeader("api-user-id") apiUserId: Long,
    ): Journey = Journey(JourneyId(IdGenerator.next()), UserId(apiUserId)).also { journey ->
        cache.addJourney(journey)
    }

    @GetMapping("/journey/{journey_id}")
    suspend fun fetch(
        @RequestHeader("api-user-id") apiUserId: Long,
        @PathVariable("journey_id") journeyId: Long
    ): Journey {
        val journey = cache.getJourneyById(JourneyId(journeyId))
        return when {
            journey == null -> throw NotFoundException("Journey not found")
            journey.userId != UserId(apiUserId) -> throw ForbiddenException("Cannot access another user's journey")
            else -> journey
        }
    }

    @GetMapping("/journey")
    suspend fun fetchForUser(
        @RequestHeader("api-user-id") userId: Long,
    ): Set<Journey> = cache.getJourneysForUser(UserId(userId))

    @DeleteMapping("/journey/{journey_id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    suspend fun delete(
        @RequestHeader("api-user-id") apiUserId: Long, @PathVariable("journey_id") journeyId: Long
    ) {
        val journey = cache.getJourneyById(JourneyId(journeyId))
            ?: throw NotFoundException("Journey with provided id was not found")

        if (journey.userId != UserId(apiUserId))
            throw ForbiddenException("User is not allowed to modify this journey")

        cache.deleteJourney(journey.id)
    }
}