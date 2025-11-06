## MVI in this app

This app follows a pragmatic MVI (Model–View–Intent) with unidirectional data flow and strict separation of UI logic (ViewModel) from domain/side-effects (Feature).

### Building blocks

- **Intent (UI → VM)**: User/UI events. Enqueued to a channel and processed in the `ViewModel`.
- **Action (VM internal)**: Reducer-friendly events used to transition state. Derived from Feature state or VM intent handling.
- **State (VM → UI)**: Immutable snapshot rendered by Compose. Exposed as `StateFlow`.
- **Effect (one-off)**: Fire-and-forget events (toasts, navigation). Emitted via `SharedFlow` or channel-backed `Flow`.
- **Feature (domain/IO)**: Encapsulates side-effects and business rules. Exposes its own `state` and `effects`, accepts feature-level intents.

### Data flow

1. UI sends `Intent` to the `ViewModel` (`_intentChannel.send`).
2. `ViewModel` processes intents and can:
   - emit `Action`s to update its own UI `State` via a reducer, and/or
   - delegate work to a `Feature` by sending feature-level intents.
3. `Feature` performs side-effects (repository, network, etc.) and emits `Action`-like results through:
   - `feature.state` (long-lived) and `feature.effects` (one-off).
4. `ViewModel` observes the `Feature` and maps those updates into its own `Action`s, reducing them into UI `State`.
5. Compose collects the `StateFlow` and renders.

### Concurrency strategy

- Intents are buffered; processing is asynchronous per intent where appropriate.
- Side-effects in `Feature` run concurrently, but state updates remain deterministic via atomic reductions:
  - `HelloWorldFeature` uses `MutableStateFlow.update { ... }` for atomic state writes.
  - A unique counter is assigned with `AtomicInteger` to avoid read–modify–write races when multiple intents arrive simultaneously.

### HelloWorldViewModel

```23:139:/Users/IvanFeneko/AndroidStudioProjects/mvi-demo/app/src/main/java/com/example/demoarchitecture/ui/helloworld/HelloWorldViewModel.kt
@HiltViewModel
class HelloWorldViewModel @Inject constructor(
    private val helloWorldFeature: HelloWorldFeature
) : ViewModel() {
    sealed class Intent { object LoadData : Intent() }
    sealed class Action {
        object Idle : Action()
        object Loading : Action()
        data class Success(val data: String) : Action()
        data class Error(val message: String) : Action()
    }
    sealed class Effect { object HelloWorldProduced : Effect(); object ErrorOccurred : Effect() }
    data class State(val helloWorld: HelloWorldState = HelloWorldState.Idle) {
        sealed class HelloWorldState {
            object Idle : HelloWorldState()
            object Loading : HelloWorldState()
            data class Success(val data: String) : HelloWorldState()
            data class Error(val message: String) : HelloWorldState()
        }
    }
    // Intents buffered via Channel; state via MutableStateFlow; effects via MutableSharedFlow
}
```

- The VM:
  - Buffers intents with a `Channel` and processes them in `viewModelScope`.
  - Maps `helloWorldFeature.state` to VM `Action`s (`Loading`, `Success`, `Error`) and reduces them to `State`.
  - For `Intent.LoadData`, delegates to `helloWorldFeature.sendIntent(ProduceHelloWorld)`.

### HelloWorldFeature

```53:148:/Users/IvanFeneko/AndroidStudioProjects/mvi-demo/app/src/main/java/com/example/demoarchitecture/features/hello/HelloWorldFeature.kt
private val _state = MutableStateFlow(State.initial())
val state: StateFlow<State> = _state
private val _effects = Channel<Effect>()
val effects: Flow<Effect> = _effects.receiveAsFlow()

init { startIntentProcessor() }

private fun startIntentProcessor() {
    featureScope.launch {
        _intentChannel.receiveAsFlow()
            .buffer(Channel.UNLIMITED)
            .collect { intent ->
                featureScope.launch {
                    try { processIntent(intent) } catch (_: Exception) { }
                }
            }
    }
}

private fun dispatchAction(action: Action) {
    _state.update { current -> reduce(current, action) }
}

private suspend fun produceHelloWorld() {
    val assignedCounter = nextCounter.incrementAndGet()
    dispatchAction(Action.LoadingStarted)
    try {
        val newMessage = repository.getHelloWorld(assignedCounter)
        dispatchAction(Action.LoadingSucceeded(newMessage, assignedCounter))
        _effects.send(Effect.HelloWorldProduced)
    } catch (e: Exception) {
        dispatchAction(Action.LoadingFailed(e.message ?: "Unknown error occurred"))
        _effects.send(Effect.ErrorOccurred)
    }
}
```

- The Feature owns:
  - Its own `State` and `Effect`s.
  - Async per-intent workers for side-effects, with atomic state reduction.

### Navigation

- Navigation is centralized in `navigation/NavGraph.kt`, exposing type-safe helpers like `navigateToHelloWorld()` and routes in a sealed `Screen`.

### Testing notes

- ViewModel reducer tests: feed `Action`s and assert `State` transitions.
- Feature tests: stub `repository`, send intents, and assert `state`/`effects` emissions.

### Cursor quick rules for this repo

- Prefer code references when discussing existing code (line-ranged blocks with path).
- For new code proposals, use standard fenced code blocks with a language tag.
- Keep UI state immutable; only mutate via reducers.
- Keep side-effects in Features; UI layers should not perform IO directly.


