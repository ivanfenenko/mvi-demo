package com.example.demoarchitecture

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.demoarchitecture.ui.theme.DemoArchitectureTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HelloWorldActivity : ComponentActivity() {
    private val viewModel: HelloWorldViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DemoArchitectureTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = { SnackbarHost(remember { SnackbarHostState() }) }
                ) { innerPadding ->
                    val uiState by viewModel.uiState.collectAsState()
                    val effect by viewModel.effects.collectAsState()

                    HelloWorldContent(
                        uiState = uiState,
                        onLoadClick = {
                            android.util.Log.d("HelloWorldActivity", "Button clicked!")
                            viewModel.processIntent(HelloWorldViewModel.Intent.LoadData)
                        },
                        modifier = Modifier.padding(innerPadding)
                    )

                    // Handle effects
                    HandleEffects(effect = effect)
                }
            }
        }
    }
}

@Composable
fun HandleEffects(effect: HelloWorldViewModel.Effect?) {
    val context = LocalContext.current

    LaunchedEffect(effect) {
        when (effect) {
            is HelloWorldViewModel.Effect.HelloWorldProduced -> {
                Toast.makeText(context, "Hello World Produced!", Toast.LENGTH_SHORT).show()
            }

            null -> {
                // do nothing
            }
        }
    }
}

@Composable
fun HelloWorldContent(
    uiState: HelloWorldViewModel.UiState,
    onLoadClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (uiState) {
            is HelloWorldViewModel.UiState.Idle -> {
                Button(onClick = onLoadClick) {
                    Text("Load Hello World")
                }
            }

            is HelloWorldViewModel.UiState.Loading -> {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(8.dp))
                Text("Loading...")
            }

            is HelloWorldViewModel.UiState.Success -> {
                Text(uiState.data)
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onLoadClick) {
                    Text("Load Again")
                }
            }

            is HelloWorldViewModel.UiState.Error -> {
                Text(uiState.message, color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onLoadClick) {
                    Text("Retry")
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Idle State")
@Composable
fun IdleHelloWorldPreview() {
    DemoArchitectureTheme {
        HelloWorldContent(uiState = HelloWorldViewModel.UiState.Idle, onLoadClick = {})
    }
}

@Preview(showBackground = true, name = "Loading State")
@Composable
fun LoadingHelloWorldPreview() {
    DemoArchitectureTheme {
        HelloWorldContent(uiState = HelloWorldViewModel.UiState.Loading, onLoadClick = {})
    }
}

@Preview(showBackground = true, name = "Success State")
@Composable
fun SuccessHelloWorldPreview() {
    DemoArchitectureTheme {
        HelloWorldContent(
            uiState = HelloWorldViewModel.UiState.Success("Hello, Preview!"),
            onLoadClick = {})
    }
}

@Preview(showBackground = true, name = "Error State")
@Composable
fun ErrorHelloWorldPreview() {
    DemoArchitectureTheme {
        HelloWorldContent(
            uiState = HelloWorldViewModel.UiState.Error("Preview Error Message"),
            onLoadClick = {})
    }
}

