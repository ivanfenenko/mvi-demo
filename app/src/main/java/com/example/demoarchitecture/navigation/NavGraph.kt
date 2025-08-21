package com.example.demoarchitecture.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.demoarchitecture.ui.helloworld.HelloWorldScreen
import com.example.demoarchitecture.ui.home.HomeScreen
import com.example.demoarchitecture.ui.second.SecondScreen

fun NavHostController.navigateToHelloWorld() = navigate(Screen.HelloWorld.route)
fun NavHostController.navigateToSecond() = navigate(Screen.Second.route)

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object HelloWorld : Screen("hello_world")
    object Second : Screen("second")
}

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToHelloWorld = { navController.navigateToHelloWorld() },
                onNavigateToSecond = { navController.navigateToSecond() }
            )
        }

        composable(Screen.HelloWorld.route) {
            HelloWorldScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Second.route) {
            SecondScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
