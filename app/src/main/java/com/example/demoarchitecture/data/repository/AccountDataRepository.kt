package com.example.demoarchitecture.data.repository

import com.example.demoarchitecture.data.model.AccountData
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class AccountDataRepository @Inject constructor() {

    suspend fun getAccountData(): AccountData {
        val randomDelayMs = Random.nextLong(from = 100, until = 501)
        delay(randomDelayMs)
        return AccountData.demo()
    }
}

