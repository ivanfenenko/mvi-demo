package com.example.demoarchitecture.data.repository

import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class HelloWorldRepository @Inject constructor() {

    suspend fun getHelloWorld(counter: Int): String {
        // Random delay: either 500ms or 3 seconds
        val randomDelay = if (counter > 3 && Random.nextBoolean()) 3000L else 500L
        delay(randomDelay)

        // Simulate potential error for demonstration
        // After counter 5, 50/50 chance of error
        if (counter > 5 && Random.nextBoolean()) {
            throw RuntimeException("Simulated repository error after 5 attempts!")
        }

        return "Hello World #$counter"
    }

}
