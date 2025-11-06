package com.example.demoarchitecture.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.demoarchitecture.ui.helloworld.HelloWorldScreen
import com.example.demoarchitecture.ui.home.HomeScreen
import com.example.demoarchitecture.ui.level1.Level1Screen
import com.example.demoarchitecture.ui.level2.Level2Screen
import com.example.demoarchitecture.ui.level3.Level3Screen
import com.example.demoarchitecture.ui.second.SecondScreen

fun NavHostController.navigateToHelloWorld() = navigate(Screen.HelloWorld.route)
fun NavHostController.navigateToSecond() = navigate(Screen.Second.route)
fun NavHostController.navigateToLevel1() = navigate(Screen.Level1.route)
fun NavHostController.navigateToLevel2() = navigate(Screen.Level2.route)
fun NavHostController.navigateToLevel3() = navigate(Screen.Level3.route)

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object HelloWorld : Screen("hello_world")
    object Second : Screen("second")
    object Level1 : Screen("level1")
    object Level2 : Screen("level2")
    object Level3 : Screen("level3")
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
                onNavigateToSecond = { navController.navigateToSecond() },
                onNavigateToLevel1 = { navController.navigateToLevel1() },
                onNavigateToLevel2 = { navController.navigateToLevel2() },
                onNavigateToLevel3 = { navController.navigateToLevel3() }
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

        composable(Screen.Level1.route) {
            Level1Screen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Level2.route) {
            Level2Screen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Level3.route) {
            Level3Screen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
