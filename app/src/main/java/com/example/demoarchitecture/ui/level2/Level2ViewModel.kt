package com.example.demoarchitecture.ui.level2

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.demoarchitecture.data.model.AccountData
import com.example.demoarchitecture.data.model.Transaction
import com.example.demoarchitecture.data.repository.AccountDataRepository
import com.example.demoarchitecture.data.repository.TransactionsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class Level2ViewModel @Inject constructor(
    val accountDataRepository: AccountDataRepository,
    val transactionsRepository: TransactionsRepository,
) : ViewModel() {

    sealed class Intent {
        object LoadData : Intent()
    }

    sealed class Action {
        object Idle : Action()
        object Loading : Action()
        data class Success(
            val account: AccountData,
            val transactions: List<Transaction>
        ) : Action()

        data class Error(val message: String) : Action()
    }

    data class State(
        val title: String = "MVI demo level 2",
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

    private val _intentChannel = Channel<Intent>()
    private val _uiState = MutableStateFlow(State())
    val uiState: StateFlow<State> = _uiState

    init {
        startIntentProcessor()
    }

    private fun startIntentProcessor() {
        viewModelScope.launch {
            _intentChannel.receiveAsFlow()
                .buffer(Channel.UNLIMITED)
                .collect { intent ->
                    try {
                        processIntent(intent)
                    } catch (_: Exception) {
                        // Swallow VM-level exceptions to keep processor alive
                    }
                }
        }
    }

    fun sendIntent(intent: Intent) {
        viewModelScope.launch { _intentChannel.send(intent) }
    }

    private fun processIntent(intent: Intent) {
        when (intent) {
            is Intent.LoadData -> loadData()
        }
    }

    private fun dispatchAction(action: Action) {
        _uiState.update {
            reduce(
                currentState = _uiState.value,
                action = action
            )
        }
    }

    private fun reduce(currentState: State, action: Action): State {
        return when (action) {
            is Action.Idle -> currentState.copy(
                screenState = State.ScreenState.Idle)

            is Action.Loading -> currentState.copy(
                screenState = State.ScreenState.Loading
            )
            is Action.Success -> currentState.copy(
                screenState = State.ScreenState.Success(
                    account = action.account,
                    transactions = action.transactions
                )
            )

            is Action.Error -> currentState.copy(
                screenState = State.ScreenState.Error(
                    message = action.message
                )
            )
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            dispatchAction(Action.Loading)
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
                dispatchAction(Action.Success(account, transactions))
            } catch (e: Exception) {
                dispatchAction(Action.Error(e.message ?: "Unknown error"))
            }
        }
    }

}

