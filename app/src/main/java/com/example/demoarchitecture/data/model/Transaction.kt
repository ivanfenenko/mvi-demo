package com.example.demoarchitecture.data.model

import java.util.UUID
import kotlin.math.round
import kotlin.random.Random

data class Transaction(
    val id: String,
    val title: String,
    val amount: Double,
    val timestampMillis: Long,
    val status: TransactionStatus
) {
    companion object {
        fun demo(
            id: String = "tx-" + UUID.randomUUID().toString(),
            title: String = randomTitle(),
            amount: Double = randomAmount(),
            timestampMillis: Long = randomTimestampWithinLastMonth(),
            status: TransactionStatus = randomStatusBiased()
        ): Transaction = Transaction(id, title, amount, timestampMillis, status)

        private fun randomTitle(): String {
            val titles = listOf(
                "Coffee",
                "Groceries",
                "Taxi",
                "Subscription",
                "Lunch",
                "Dinner",
                "Snacks",
                "Books",
                "Electronics",
                "Gym"
            )
            return titles.random()
        }

        private fun randomAmount(): Double {
            // Random between 1.00 and 200.00, rounded to 2 decimals
            val value = Random.nextDouble(from = 1.0, until = 200.0)
            return round(value * 100.0) / 100.0
        }

        private fun randomTimestampWithinLastMonth(): Long {
            val now = System.currentTimeMillis()
            val thirtyDaysMillis = 30L * 24L * 60L * 60L * 1000L
            val lowerBound = now - thirtyDaysMillis
            // Uniform between lowerBound (inclusive) and now (exclusive)
            return Random.nextLong(from = lowerBound, until = now)
        }

        private fun randomStatusBiased(): TransactionStatus {
            // ~80% Completed, 10% Pending, 10% Failed
            return when (Random.nextInt(100)) {
                in 0..79 -> TransactionStatus.COMPLETED
                in 80..89 -> TransactionStatus.PENDING
                else -> TransactionStatus.FAILED
            }
        }
    }
}

enum class TransactionStatus { PENDING, COMPLETED, FAILED }