package com.hopkins.fitlink.feature.workout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hopkins.fitlink.core.ui.ControlStepper
import kotlin.math.roundToLong

@Composable
fun WorkoutScreen(
    viewModel: WorkoutScreenViewModel = hiltViewModel()
) {
    val speed = viewModel.speed.collectAsStateWithLifecycle().value
    val equipmentType = viewModel.equipmentType.collectAsStateWithLifecycle().value
    val notificationStatus = viewModel.notificationStatus.collectAsStateWithLifecycle().value
    val connectionState = viewModel.connectionState.collectAsStateWithLifecycle().value

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(notificationStatus.toString())
            Text(equipmentType.toString())
            Text(connectionState.toString())
            ControlStepper(
                value = speed.toString().format("%.1f"),
                modifier = Modifier.width(98.dp),
                heading = "Speed",
                unit = "MPH"
            )
            Button(
                onClick = { viewModel.updateSpeed() }
            ) {
                Text("Update speed")
            }
        }
    }

}