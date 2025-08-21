package com.example.demoarchitecture.ui.helloworld

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.demoarchitecture.features.hello.HelloWorldFeature
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HelloWorldViewModel @Inject constructor(
    private val helloWorldFeature: HelloWorldFeature
) : ViewModel() {

    sealed class Intent {
        object LoadData : Intent()
    }

    sealed class Action {
        object Idle : Action()
        object Loading : Action()
        data class Success(val data: String) : Action()
        data class Error(val message: String) : Action()
    }

    sealed class UiState {
        object Idle : UiState()
        object Loading : UiState()
        data class Success(val data: String) : UiState()
        data class Error(val message: String) : UiState()
    }

    sealed class Effect {
        object HelloWorldProduced : Effect()
        object ErrorOccurred : Effect()
    }

    private val _intentChannel = Channel<Intent>()
    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState

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
                    } catch (e: Exception) {
                        // Handle ViewModel-level errors
                    }
                }
        }
    }

    private fun startFeatureObservers() {
        viewModelScope.launch {
            helloWorldFeature.state.collect { featureState ->
                val action = when {
                    featureState.isLoading -> Action.Loading
                    featureState.error != null -> Action.Error(featureState.error)
                    else -> Action.Success(featureState.message)
                }
                dispatchAction(action)
            }
        }

        viewModelScope.launch {
            helloWorldFeature.effects.collect { featureEffect ->
                when (featureEffect) {
                    is HelloWorldFeature.Effect.HelloWorldProduced -> {
                        _effects.emit(Effect.HelloWorldProduced)
                    }

                    is HelloWorldFeature.Effect.ErrorOccurred -> {
                        _effects.emit(Effect.ErrorOccurred)
                    }
                }
            }
        }
    }

    fun sendIntent(intent: Intent) {
        viewModelScope.launch {
            _intentChannel.send(intent)
        }
    }

    private fun dispatchAction(action: Action) {
        val currentState = _uiState.value
        val newState = reduce(currentState, action)
        _uiState.value = newState
    }

    private fun reduce(currentState: UiState, action: Action): UiState {
        return when (action) {
            is Action.Idle -> UiState.Idle
            is Action.Loading -> UiState.Loading
            is Action.Success -> UiState.Success(action.data)
            is Action.Error -> UiState.Error(action.message)
        }
    }

    private fun processIntent(intent: Intent) {
        when (intent) {
            is Intent.LoadData -> {
                helloWorldFeature.sendIntent(HelloWorldFeature.Intent.ProduceHelloWorld)
            }
        }
        // Add more intent processing here as needed
    }
}