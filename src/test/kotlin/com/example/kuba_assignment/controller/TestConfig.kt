package com.example.kuba_assignment.controller

import com.example.kuba_assignment.cache.InMemoryJourneyCache
import com.example.kuba_assignment.cache.JourneyCache
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
class TestConfig {
    @Bean
    @Primary
    fun testJourneyCache(): JourneyCache = InMemoryJourneyCache()
}