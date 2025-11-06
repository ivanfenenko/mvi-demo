package com.example.demoarchitecture.ui.level3

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.demoarchitecture.ui.level3.Level3ViewModel.State.ScreenState
import kotlinx.coroutines.flow.collectLatest
import android.widget.Toast

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Level3Screen(
    onNavigateBack: () -> Unit,
    viewModel: Level3ViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                is Level3ViewModel.Effect.ErrorOccurred -> {
                    Toast.makeText(context, "An error occurred", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = state.title) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { innerPadding ->
        when (val screen = state.screenState) {
            is ScreenState.Idle -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = state.title,
                        style = MaterialTheme.typography.headlineMedium
                    )

                    Spacer(modifier = Modifier.size(16.dp))

                    Button(onClick = { viewModel.sendIntent(Level3ViewModel.Intent.LoadData) }) {
                        Text("Load")
                    }

                    Spacer(modifier = Modifier.size(24.dp))

                    Text("Tap Load to fetch data")
                }
            }

            is ScreenState.Loading -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                }
            }

            is ScreenState.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = screen.message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            is ScreenState.Success -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(vertical = 16.dp)
                ) {
                    Button(
                        onClick = { viewModel.sendIntent(Level3ViewModel.Intent.RefreshTransactions) },
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                    ) {
                        Text("Refresh Transactions")
                    }

                    Spacer(modifier = Modifier.size(12.dp))

                    AccountDataCard(
                        data = screen.account,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.size(16.dp))

                    TransactionsCard(
                        transactions = screen.transactions,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(horizontal = 16.dp)
                    )
                }
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
private fun AccountDataCard(
    data: com.example.demoarchitecture.data.model.AccountData,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = data.accountName, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.size(8.dp))
            Text(text = "Current balance: $${String.format("%.2f", data.currentBalance)}")
            Text(text = "Available: $${String.format("%.2f", data.amountAvailable)}")
            Text(text = "Credit limit: $${String.format("%.2f", data.creditLimit)}")
        }
    }
}

@Composable
private fun TransactionsCard(
    transactions: List<com.example.demoarchitecture.data.model.Transaction>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(16.dp)
        ) {
            Text(
                text = "Transactions",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.size(8.dp))

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(transactions) { tx ->
                    TransactionRow(tx)
                }
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
private fun TransactionRow(tx: com.example.demoarchitecture.data.model.Transaction) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(text = tx.title, style = MaterialTheme.typography.bodyLarge)
        Text(
            text = "$${String.format("%.2f", tx.amount)} • ${tx.status}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}


