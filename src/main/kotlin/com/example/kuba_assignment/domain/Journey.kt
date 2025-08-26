package com.example.kuba_assignment.domain

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class Journey @JsonCreator constructor(
    @JsonProperty("id") val id: JourneyId,
    @JsonProperty("userId") val userId: UserId
)
