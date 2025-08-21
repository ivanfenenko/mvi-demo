package com.example.demoarchitecture.ui.helloworld

import android.widget.Toast
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

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

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (val helloWorldState = uiState.helloWorld) {
                is HelloWorldViewModel.State.HelloWorldState.Idle -> {
                    Button(
                        onClick = { viewModel.sendIntent(HelloWorldViewModel.Intent.LoadData) }
                    ) {
                        Text("Load Hello World")
                    }
                }

                is HelloWorldViewModel.State.HelloWorldState.Loading -> {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Loading...")
                }

                is HelloWorldViewModel.State.HelloWorldState.Success -> {
                    Text(helloWorldState.data)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { viewModel.sendIntent(HelloWorldViewModel.Intent.LoadData) }
                    ) {
                        Text("Load Again")
                    }
                }

                is HelloWorldViewModel.State.HelloWorldState.Error -> {
                    Text(helloWorldState.message, color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { viewModel.sendIntent(HelloWorldViewModel.Intent.LoadData) }
                    ) {
                        Text("Retry")
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
