package com.example.kuba_assignment.util

import java.util.concurrent.atomic.AtomicLong

object IdGenerator {
    private val counter = AtomicLong(1)

    fun next(): Long = counter.getAndIncrement()
}