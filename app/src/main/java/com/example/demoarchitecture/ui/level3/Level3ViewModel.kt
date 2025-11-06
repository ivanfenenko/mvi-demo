package com.example.demoarchitecture.ui.level3

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.demoarchitecture.data.model.AccountData
import com.example.demoarchitecture.data.model.Transaction
import com.example.demoarchitecture.features.home.HomeFeature
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class Level3ViewModel @Inject constructor(
    val homeFeature: HomeFeature,
) : ViewModel() {

    sealed class Intent {
        object LoadData : Intent()
        object RefreshTransactions : Intent()
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

    sealed class Effect {
        object ErrorOccurred : Effect()
    }

    data class State(
        val title: String = "MVI demo level 3",
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
    private val _effects = MutableSharedFlow<Effect>(replay = 0)
    val effects: SharedFlow<Effect> = _effects.asSharedFlow()

    init {
        startIntentProcessor()
        startFeatureObservers()
    }

    private fun startIntentProcessor() {
        viewModelScope.launch {
            _intentChannel.receiveAsFlow()
                .buffer(Channel.UNLIMITED)
                .collect { intent ->
                    try {
                        processIntent(intent)
                    } catch (_: Exception) {
                        // Keep processor alive
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
            is Intent.RefreshTransactions -> refreshTransactions()
        }
    }

    private fun startFeatureObservers() {
        viewModelScope.launch {
            homeFeature.state.collect { featureState ->
                when {
                    featureState.error != null -> {
                        val hasDataLoaded = featureState.account != null || featureState.transactions.isNotEmpty()
                        if (!hasDataLoaded) {
                            dispatchAction(Action.Error(featureState.error))
                        }
                        _effects.emit(Effect.ErrorOccurred)
                    }

                    featureState.isLoadingAccount -> {
                        dispatchAction(Action.Loading)
                    }

                    featureState.account != null -> {
                        dispatchAction(
                            Action.Success(
                                account = featureState.account,
                                transactions = featureState.transactions
                            )
                        )
                    }

                    else -> {
                        // no op
                    }
                }
            }
        }
    }

    private fun dispatchAction(action: Action) {
        _uiState.update {
            reduce(currentState = _uiState.value, action = action)
        }
    }

    private fun reduce(currentState: State, action: Action): State {
        return when (action) {
            is Action.Idle -> currentState.copy(
                screenState = State.ScreenState.Idle
            )

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
                screenState = State.ScreenState.Error(message = action.message)
            )
        }
    }

    private fun loadData() {
        homeFeature.sendIntent(HomeFeature.Intent.LoadHomeData)
    }

    private fun refreshTransactions() {
        homeFeature.sendIntent(HomeFeature.Intent.ReloadTransactions)
    }
}


