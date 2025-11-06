package com.example.demoarchitecture.data.model

data class AccountData(
    val accountName: String,
    val currentBalance: Double,
    val amountAvailable: Double,
    val creditLimit: Double
) {
    companion object {
        fun demo(): AccountData = AccountData(
            accountName = "Alex Johnson",
            creditLimit = 5000.00,
            currentBalance = 1234.56,
            amountAvailable = 3765.44
        )
    }
}