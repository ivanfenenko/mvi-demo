package com.example.demoarchitecture

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.demoarchitecture.features.hello.HelloWorldFeature
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
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

    private val _effects = MutableSharedFlow<Effect>(replay = 0)
    val effects: SharedFlow<Effect> = _effects.asSharedFlow()

    init {
        // Observe feature state and effects
        viewModelScope.launch {
            // Observe feature state
            helloWorldFeature.state.collect { featureState ->
                _uiState.value = UiState.Success(featureState.message)
            }
        }
        
        // Observe feature effects
        viewModelScope.launch {
            helloWorldFeature.effects.collect { featureEffect ->
                when (featureEffect) {
                    is HelloWorldFeature.Effect.HelloWorldProduced -> {
                        _effects.emit(Effect.HelloWorldProduced)
                    }
                }
            }
        }
    }

    fun processIntent(intent: Intent) {
        when (intent) {
            is Intent.LoadData -> {
                // Delegate to feature in a coroutine
                viewModelScope.launch {
                    helloWorldFeature.processIntent(HelloWorldFeature.Intent.ProduceHelloWorld)
                }
            }
        }
    }
}
