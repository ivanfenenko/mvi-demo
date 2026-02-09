package com.example.demoarchitecture.ui.helloworld

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.demoarchitecture.ui.theme.DemoArchitectureTheme

@Composable
fun HelloWorldScreen(
    onNavigateBack: () -> Unit,
    viewModel: HelloWorldViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is HelloWorldViewModel.Effect.HelloWorldProduced -> {
                    Toast.makeText(context, "Hello World Produced!", Toast.LENGTH_SHORT).show()
                }

                is HelloWorldViewModel.Effect.ErrorOccurred -> {
                    Toast.makeText(context, "Error occurred!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    HelloWorldContent(
        state = uiState.helloWorld,
        onNavigateBack = onNavigateBack,
        onLoadData = { viewModel.sendIntent(HelloWorldViewModel.Intent.LoadData) }
    )
}

@Composable
internal fun HelloWorldContent(
    state: HelloWorldViewModel.State.HelloWorldState,
    onNavigateBack: () -> Unit,
    onLoadData: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (state) {
                is HelloWorldViewModel.State.HelloWorldState.Idle -> {
                    Text(
                        text = "Ready to load",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onLoadData,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Start Loading")
                    }
                }

                is HelloWorldViewModel.State.HelloWorldState.Loading -> {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Loading data...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                is HelloWorldViewModel.State.HelloWorldState.Success -> {
                    Text(
                        text = "✓ ${state.data}",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onLoadData,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Reload Data")
                    }
                }

                is HelloWorldViewModel.State.HelloWorldState.Error -> {
                    Text(
                        text = "✗ ${state.message}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onLoadData,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Try Again")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onNavigateBack
            ) {
                Text("Go Back")
            }
        }
    }
}

@Preview(showBackground = true, name = "Hello World - Idle")
@Composable
fun HelloWorldIdlePreview() {
    DemoArchitectureTheme {
        HelloWorldContent(
            state = HelloWorldViewModel.State.HelloWorldState.Idle,
            onNavigateBack = {},
            onLoadData = {}
        )
    }
}

@Preview(showBackground = true, name = "Hello World - Loading")
@Composable
fun HelloWorldLoadingPreview() {
    DemoArchitectureTheme {
        HelloWorldContent(
            state = HelloWorldViewModel.State.HelloWorldState.Loading,
            onNavigateBack = {},
            onLoadData = {}
        )
    }
}

@Preview(showBackground = true, name = "Hello World - Success")
@Composable
fun HelloWorldSuccessPreview() {
    DemoArchitectureTheme {
        HelloWorldContent(
            state = HelloWorldViewModel.State.HelloWorldState.Success("Hello World!"),
            onNavigateBack = {},
            onLoadData = {}
        )
    }
}

@Preview(showBackground = true, name = "Hello World - Error")
@Composable
fun HelloWorldErrorPreview() {
    DemoArchitectureTheme {
        HelloWorldContent(
            state = HelloWorldViewModel.State.HelloWorldState.Error("Something went wrong"),
            onNavigateBack = {},
            onLoadData = {}
        )
    }
}
