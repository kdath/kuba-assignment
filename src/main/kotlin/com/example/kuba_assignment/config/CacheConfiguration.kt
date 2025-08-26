package com.example.kuba_assignment.config

import com.example.kuba_assignment.cache.InMemoryJourneyCache
import com.example.kuba_assignment.cache.JourneyCache
import com.example.kuba_assignment.cache.RedisJourneyCache
import com.example.kuba_assignment.domain.Journey
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
@Profile("!test")
class CacheConfiguration {

    @Bean
    fun reactiveJourneyRedisTemplate(factory: ReactiveRedisConnectionFactory): ReactiveRedisTemplate<String, Journey> {
        val serializer = Jackson2JsonRedisSerializer(Journey::class.java)
        val stringSerializer = StringRedisSerializer()

        val context = RedisSerializationContext
            .newSerializationContext<String, Journey>(stringSerializer)
            .value(serializer)
            .build()

        return ReactiveRedisTemplate(factory, context)
    }

    @Bean
    fun journeyCache(
        @Value("\${cache.type:inmemory}") cacheType: String,
        redisTemplate: ReactiveRedisTemplate<String, Journey>
    ): JourneyCache = when (cacheType.lowercase()) {
        "redis" -> RedisJourneyCache(redisTemplate)
        else -> InMemoryJourneyCache()
    }


}