package com.example.demoarchitecture.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object HelloWorld : Screen("hello_world")
    object Second : Screen("second")
}
