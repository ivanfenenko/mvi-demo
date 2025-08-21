package com.example.demoarchitecture.features.hello

import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@ViewModelScoped
class HelloWorldFeature @Inject constructor(
    private val featureScope: CoroutineScope
) {

    data class State(
        val message: String,
        val counter: Int
    ) {
        companion object {
            fun initial() = State(
                message = "Hello World #0",
                counter = 0
            )
        }
    }

    sealed class Intent {
        object ProduceHelloWorld : Intent()
    }

    sealed class Effect {
        object HelloWorldProduced : Effect()
    }

    private val _intentChannel = Channel<Intent>()
    val intentChannel: SendChannel<Intent> = _intentChannel

    private val _state = MutableStateFlow(State.initial())
    val state: StateFlow<State> = _state

    private val _effects = Channel<Effect>()
    val effects: Flow<Effect> = _effects.receiveAsFlow()

    init {
        startIntentProcessor()
    }

    private fun startIntentProcessor() {
        featureScope.launch {
            Timber.d("Intent processor started")
            for (intent in _intentChannel) {
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
        val newMessage = "Hello World #$newCounter"

        Timber.d("Producing Hello World #$newCounter")

        _state.value = currentState.copy(
            message = newMessage,
            counter = newCounter
        )

        _effects.send(Effect.HelloWorldProduced)
        Timber.d("Effect sent: HelloWorldProduced")
    }

}
