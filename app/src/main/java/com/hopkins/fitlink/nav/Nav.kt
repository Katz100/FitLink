package com.hopkins.fitlink.nav

import android.annotation.SuppressLint
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hopkins.fitlink.feature.workout.WorkoutScreen
import com.hopkins.fitlink.feature.home.HomeScreen

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Nav() {
    val navController = rememberNavController()
    val startDestination = Screen.Home
    var selectedDestination by rememberSaveable {
        mutableIntStateOf(Screen.BottomDestinations.indexOf(startDestination))
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val isBottomDestination = currentDestination?.hasRoute<Screen.ActiveWorkout>() != true

    Scaffold(
        bottomBar = {
            if (isBottomDestination) {
                NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
                    Screen.BottomDestinations.forEachIndexed { index, destination ->
                        NavigationBarItem(
                            selected = selectedDestination == index,
                            onClick = {
                                navController.navigate(destination)
                                selectedDestination = index
                            },
                            icon = {
                                Icon(
                                    Icons.Default.Build,
                                    contentDescription = "in progress"
                                )
                            },
                            label = {
                                when (destination) {
                                    is Screen.ActiveWorkout -> Text("")
                                    Screen.History -> Text("History")
                                    Screen.Home -> Text("Connect")
                                    Screen.Settings -> Text("Settings")
                                    Screen.WorkoutSummary -> Text("Workout Summary")
                                }
                            }
                        )
                    }
                }
            }
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = startDestination
        ) {
            composable<Screen.Home> {
                HomeScreen(
                    onDeviceClicked = {
                        navController.navigate(Screen.ActiveWorkout(it))
                    }
                )
            }

            composable<Screen.ActiveWorkout> {
                WorkoutScreen(
                    onWorkoutEnded = {
                        navController.popBackStack()
                    }
                )
            }

            composable<Screen.History> {
                Text("History")
            }

            composable<Screen.Settings> {
                Text("Settings")
            }
        }
    }
}