package com.hopkins.fitlink.feature.workout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun WorkoutScreen(
    viewModel: WorkoutScreenViewModel = hiltViewModel()
) {
    val speed = viewModel.speed.collectAsStateWithLifecycle().value

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(speed.toString())
        Button(
            onClick = { viewModel.updateSpeed() }
        ) {
            Text("Update speed")
        }
    }
}