package com.example.demoarchitecture.data.repository

import com.example.demoarchitecture.data.model.Transaction
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class TransactionsRepository @Inject constructor() {

    suspend fun getTransactionHistory(): List<Transaction> {
        val randomDelayMs = Random.nextLong(from = 100, until = 501)
        delay(randomDelayMs)
        if (Random.nextDouble() < 0.3) {
            throw RuntimeException("Simulated transactions error")
        }
        val items = List(size = 10) { Transaction.demo() }
        return items.sortedByDescending { it.timestampMillis }
    }
}

