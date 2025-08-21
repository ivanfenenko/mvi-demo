package com.example.demoarchitecture.features.hello

import com.example.demoarchitecture.data.repository.HelloWorldRepository
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@ViewModelScoped
class HelloWorldFeature @Inject constructor(
    private val featureScope: CoroutineScope,
    private val repository: HelloWorldRepository
) {

    data class State(
        val message: String,
        val counter: Int,
        val isLoading: Boolean = false,
        val error: String? = null
    ) {
        companion object {
            fun initial() = State(
                message = "Hello World #0",
                counter = 0,
                isLoading = false,
                error = null
            )
        }
    }

    sealed class Intent {
        object ProduceHelloWorld : Intent()
    }

    sealed class Action {
        object LoadingStarted : Action()
        data class LoadingSucceeded(val message: String, val counter: Int) : Action()
        data class LoadingFailed(val error: String) : Action()
    }

    sealed class Effect {
        object HelloWorldProduced : Effect()
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
        featureScope.launch {
            _intentChannel.send(intent)
        }
    }

    private fun dispatchAction(action: Action) {
        val currentState = _state.value
        val newState = reduce(currentState, action)
        _state.value = newState
    }

    private fun reduce(currentState: State, action: Action): State {
        return when (action) {
            is Action.LoadingStarted -> currentState.copy(
                isLoading = true,
                error = null
            )
            is Action.LoadingSucceeded -> currentState.copy(
                message = action.message,
                counter = action.counter,
                isLoading = false,
                error = null
            )
            is Action.LoadingFailed -> currentState.copy(
                isLoading = false,
                error = action.error
            )
        }
    }

    private fun startIntentProcessor() {
        featureScope.launch {
            _intentChannel.receiveAsFlow()
                .buffer(Channel.UNLIMITED)
                .collect { intent ->
                    try {
                        processIntent(intent)
                    } catch (e: Exception) {
                        Timber.e(e, "Error processing intent: $intent")
                    }
                }
        }
    }

    private suspend fun processIntent(intent: Intent) {
        Timber.d("Processing intent: $intent")
        when (intent) {
            is Intent.ProduceHelloWorld -> {
                produceHelloWorld()
            }
        }
    }

    private suspend fun produceHelloWorld() {
        val currentState = _state.value
        val newCounter = currentState.counter + 1
        
        // Dispatch loading started action
        dispatchAction(Action.LoadingStarted)
        
        try {
            Timber.d("Fetching Hello World #$newCounter from repository")
            
            // Fetch data from repository
            val newMessage = repository.getHelloWorld(newCounter)
            
            // Dispatch success action
            dispatchAction(Action.LoadingSucceeded(newMessage, newCounter))
            
            // Emit success effect
            _effects.send(Effect.HelloWorldProduced)
            Timber.d("Effect sent: HelloWorldProduced")
            
        } catch (e: Exception) {
            Timber.e(e, "Repository error: ${e.message}")
            
            // Dispatch error action
            dispatchAction(Action.LoadingFailed(e.message ?: "Unknown error occurred"))
            
            // Emit error effect
            _effects.send(Effect.ErrorOccurred)
            Timber.d("Effect sent: ErrorOccurred")
        }
    }

}
