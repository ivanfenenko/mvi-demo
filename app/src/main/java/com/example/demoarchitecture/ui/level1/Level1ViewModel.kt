package com.example.demoarchitecture.ui.level1

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.demoarchitecture.data.model.AccountData
import com.example.demoarchitecture.data.model.Transaction
import com.example.demoarchitecture.data.repository.AccountDataRepository
import com.example.demoarchitecture.data.repository.TransactionsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class Level1ViewModel @Inject constructor(
    val accountDataRepository: AccountDataRepository,
    val transactionsRepository: TransactionsRepository,
) : ViewModel() {

    sealed class Intent {
        object LoadData : Intent()
    }

    data class State(
        val title: String = "MVI demo level 1",
        val screenState: ScreenState = ScreenState.Idle
    ) {
        sealed class ScreenState {
            object Idle : ScreenState()
            object Loading : ScreenState()
            data class Success(
                val account: AccountData,
                val transactions: List<Transaction>
            ) : ScreenState()

            data class Error(val message: String) : ScreenState()
        }
    }

    private val _uiState = MutableStateFlow(State())
    val uiState: StateFlow<State> = _uiState

    fun sendIntent(intent: Intent) {
        when (intent) {
            is Intent.LoadData -> loadData()
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(screenState = State.ScreenState.Loading)
            try {
                val (account, transactions) = coroutineScope {
                    val accountDeferred = async(Dispatchers.IO) {
                        accountDataRepository.getAccountData()
                    }
                    val transactionsDeferred = async(Dispatchers.IO) {
                        transactionsRepository.getTransactionHistory()
                    }
                    accountDeferred.await() to transactionsDeferred.await()
                }
                _uiState.value = _uiState.value.copy(
                    screenState = State.ScreenState.Success(account, transactions)
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    screenState = State.ScreenState.Error(e.message ?: "Unknown error")
                )
            }
        }
    }

}


