package com.example.demoarchitecture.features.home

import com.example.demoarchitecture.data.model.AccountData
import com.example.demoarchitecture.data.model.Transaction
import com.example.demoarchitecture.data.repository.AccountDataRepository
import com.example.demoarchitecture.data.repository.TransactionsRepository
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@ViewModelScoped
class HomeFeature @Inject constructor(
    private val featureScope: CoroutineScope,
    private val accountDataRepository: AccountDataRepository,
    private val transactionsRepository: TransactionsRepository,
) {

    data class State(
        val account: AccountData? = null,
        val transactions: List<Transaction> = emptyList(),
        val isLoadingAccount: Boolean = false,
        val isLoadingTransactions: Boolean = false,
        val error: String? = null
    ) {
        companion object {
            fun initial() = State(
                account = null,
                transactions = emptyList(),
                isLoadingAccount = false,
                isLoadingTransactions = false,
                error = null
            )
        }
    }

    sealed class Intent {
        object LoadHomeData : Intent()
        object ReloadTransactions : Intent()
    }

    sealed class Action {
        object LoadingAccountStarted : Action()
        object LoadingTransactionsStarted : Action()
        data class AccountLoaded(val account: AccountData) : Action()
        data class TransactionsLoaded(val transactions: List<Transaction>) : Action()
        data class LoadingFailed(val error: String) : Action()
    }

    sealed class Effect {
        object ErrorOccurred : Effect()
    }

    private val _intentChannel = Channel<Intent>()
    private val _state = MutableStateFlow(State.initial())
    val state: StateFlow<State> = _state
    private val _effects = Channel<Effect>()
    val effects: Flow<Effect> = _effects.receiveAsFlow()

    init {
        startIntentProcessor()
    }

    fun sendIntent(intent: Intent) {
        featureScope.launch { _intentChannel.send(intent) }
    }

    private fun dispatchAction(action: Action) {
        _state.update { current -> reduce(current, action) }
    }

    private fun reduce(currentState: State, action: Action): State {
        return when (action) {
            is Action.LoadingAccountStarted -> currentState.copy(
                isLoadingAccount = true,
                error = null
            )

            is Action.LoadingTransactionsStarted -> currentState.copy(
                isLoadingTransactions = true,
                error = null
            )

            is Action.AccountLoaded -> currentState.copy(
                account = action.account,
                isLoadingAccount = false
            )

            is Action.TransactionsLoaded -> currentState.copy(
                transactions = action.transactions,
                isLoadingTransactions = false
            )

            is Action.LoadingFailed -> currentState.copy(
                isLoadingAccount = false,
                isLoadingTransactions = false,
                error = action.error
            )
        }
    }

    private fun startIntentProcessor() {
        featureScope.launch {
            _intentChannel.receiveAsFlow()
                .buffer(Channel.UNLIMITED)
                .collect { intent ->
                    featureScope.launch {
                        try {
                            processIntent(intent)
                        } catch (e: Exception) {
                            Timber.e(e, "Error processing intent: $intent")
                        }
                    }
                }
        }
    }

    private fun processIntent(intent: Intent) {
        Timber.d("Processing intent: $intent")
        when (intent) {
            is Intent.LoadHomeData -> {
                featureScope.launch { loadAccount() }
                featureScope.launch { loadTransactions() }
            }

            is Intent.ReloadTransactions -> {
                featureScope.launch { loadTransactions() }
            }
        }
    }

    private suspend fun loadAccount() {
        dispatchAction(Action.LoadingAccountStarted)
        Timber.d("Fetching account")
        try {
            val account = coroutineScope {
                async(Dispatchers.IO) {
                    accountDataRepository.getAccountData()
                }.await()
            }
            dispatchAction(Action.AccountLoaded(account))
        } catch (e: Exception) {
            Timber.e(e, "Account load error: ${e.message}")
            dispatchAction(Action.LoadingFailed(e.message ?: "Unknown error occurred"))
            _effects.send(Effect.ErrorOccurred)
        }
    }

    private suspend fun loadTransactions() {
        dispatchAction(Action.LoadingTransactionsStarted)
        Timber.d("Fetching transactions")
        try {
            val transactions = coroutineScope {
                async(Dispatchers.IO) {
                    transactionsRepository.getTransactionHistory()
                }.await()
            }
            dispatchAction(Action.TransactionsLoaded(transactions))
        } catch (e: Exception) {
            Timber.e(e, "Transactions load error: ${e.message}")
            dispatchAction(Action.LoadingFailed(e.message ?: "Unknown error occurred"))
            _effects.send(Effect.ErrorOccurred)
        }
    }
}