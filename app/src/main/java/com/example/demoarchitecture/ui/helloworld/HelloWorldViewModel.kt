package com.example.demoarchitecture.ui.helloworld

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.demoarchitecture.features.hello.HelloWorldFeature
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HelloWorldViewModel @Inject constructor(
    private val helloWorldFeature: HelloWorldFeature
) : ViewModel() {

    sealed class Intent {
        object LoadData : Intent()
    }

    sealed class UiState {
        object Idle : UiState()
        object Loading : UiState()
        data class Success(val data: String) : UiState()
        data class Error(val message: String) : UiState()
    }

    sealed class Effect {
        object HelloWorldProduced : Effect()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState

    private val _effects = MutableStateFlow<Effect?>(null)
    val effects: StateFlow<Effect?> = _effects

    init {
        // Feature automatically starts processing intents - no manual initialization needed!

        // Observe feature state
        viewModelScope.launch {
            helloWorldFeature.state.collect { featureState ->
                _uiState.value = UiState.Success(featureState.message)
            }
        }

        // Observe feature effects
        viewModelScope.launch {
            helloWorldFeature.effects.collect { featureEffect ->
                when (featureEffect) {
                    is HelloWorldFeature.Effect.HelloWorldProduced -> {
                        _effects.value = Effect.HelloWorldProduced
                        // Clear effect after it's been emitted
                        _effects.value = null
                    }
                }
            }
        }
    }

    fun processIntent(intent: Intent) {
        when (intent) {
            is Intent.LoadData -> {
                viewModelScope.launch {
                    helloWorldFeature.intentChannel.send(HelloWorldFeature.Intent.ProduceHelloWorld)
                }
            }
        }
    }

}