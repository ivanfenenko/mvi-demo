package com.example.demoarchitecture.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    onNavigateToHelloWorld: () -> Unit,
    onNavigateToSecond: () -> Unit,
    onNavigateToLevel1: () -> Unit,
    onNavigateToLevel2: () -> Unit,
    onNavigateToLevel3: () -> Unit,
) {
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
            Text(
                text = "MVI demo",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(onClick = { onNavigateToLevel1() }) {
                Text("Level 1")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { onNavigateToLevel2() }) {
                Text("Level 2")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { onNavigateToLevel3() }) {
                Text("Level 3")
            }
        }
    }
}
