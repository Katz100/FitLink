package com.hopkins.fitlink.nav

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hopkins.fitlink.feature.workout.WorkoutScreen
import com.hopkins.fitlink.feature.home.HomeScreen

@Composable
fun Nav() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Home
    ) {
        composable<Screen.Home> {
            HomeScreen(
                onDeviceClicked = {
                    navController.navigate(Screen.ActiveWorkout(it))
                }
            )
        }

        composable<Screen.ActiveWorkout> {
            WorkoutScreen()
        }

    }
}