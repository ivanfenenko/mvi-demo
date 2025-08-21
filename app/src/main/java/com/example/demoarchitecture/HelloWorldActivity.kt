package com.example.demoarchitecture

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.demoarchitecture.ui.helloworld.HelloWorldScreen
import com.example.demoarchitecture.ui.home.HomeScreen
import com.example.demoarchitecture.ui.second.SecondScreen
import com.example.demoarchitecture.ui.theme.DemoArchitectureTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HelloWorldActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DemoArchitectureTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(
                onNavigateToHelloWorld = { navController.navigate("hello_world") },
                onNavigateToSecond = { navController.navigate("second") }
            )
        }

        composable("hello_world") {
            HelloWorldScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("second") {
            SecondScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

