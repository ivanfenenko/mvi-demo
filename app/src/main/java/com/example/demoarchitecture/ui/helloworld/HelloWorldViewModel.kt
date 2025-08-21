package com.example.demoarchitecture.ui.helloworld

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.demoarchitecture.features.hello.HelloWorldFeature
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
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
        object ErrorOccurred : Effect()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState

    private val _effects = MutableSharedFlow<Effect>(replay = 0)
    val effects: SharedFlow<Effect> = _effects.asSharedFlow()

    init {
        viewModelScope.launch {
            helloWorldFeature.state.collect { featureState ->
                _uiState.value = when {
                    featureState.isLoading -> UiState.Loading
                    featureState.error != null -> UiState.Error(featureState.error)
                    else -> UiState.Success(featureState.message)
                }
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