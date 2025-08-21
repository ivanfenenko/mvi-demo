package com.example.demoarchitecture.features.hello

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HelloWorldFeature @Inject constructor() {


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

    private val _state = MutableStateFlow(State.initial())
    val state: StateFlow<State> = _state

    private val _effects = MutableSharedFlow<Effect>(replay = 0)
    val effects: SharedFlow<Effect> = _effects.asSharedFlow()

    fun processIntent(intent: Intent) {
        when (intent) {
            is Intent.ProduceHelloWorld -> {
                produceHelloWorld()
            }
        }
    }

    private fun produceHelloWorld() {
        val currentState = _state.value
        val newCounter = currentState.counter + 1
        val newMessage = "Hello World #$newCounter"

        // Update state
        _state.value = currentState.copy(
            message = newMessage,
            counter = newCounter
        )

        // Emit effect (one-time event)
        _effects.tryEmit(Effect.HelloWorldProduced)
    }

    fun getCurrentState(): State = _state.value

    fun reset() {
        _state.value = State.initial()
    }
}
