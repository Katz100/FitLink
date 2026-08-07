package com.hopkins.fitlink.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.hopkins.fitlink.core.data.ConnectionStatus
import com.hopkins.fitlink.core.ftms.domain.model.MachineUiState
import com.hopkins.fitlink.core.ftms.util.formatSeconds
import com.hopkins.fitlink.ui.theme.FitLinkTheme
import java.util.Locale

@Composable
fun TreadmillView(
    machineState: MachineUiState.TreadmillMachine,
    connectionStatus: ConnectionStatus,
    modifier: Modifier = Modifier
) {
    var showAdditionalDetailsForTime by remember { mutableStateOf(false) }
    var showAdditionalDetailsForCalories by remember { mutableStateOf(false) }
    var showAdditionalDetailsForHeartRate by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            modifier = Modifier.align(Alignment.TopCenter),
            text = connectionStatus.toString(),
            color = when (connectionStatus) {
                ConnectionStatus.Connected -> Color.Green
                is ConnectionStatus.ConnectionError -> Color.Red
                ConnectionStatus.ConnectionLoading -> Color.Black
                ConnectionStatus.Disconnected -> Color.Yellow
            }
        )

        Row(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            ControlStepper(
                modifier = Modifier.weight(1f),
                heading = "Inclination",
                unit = "%",
                value = machineState.inclination?.toString() ?: "--"
            )

            Row(
                modifier = Modifier.weight(3f),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Bottom
            ){
                KpiCard(
                    statItems = listOf(
                        hashMapOf("Elapsed Time" to formatSeconds(machineState.elapsedTime)),
                        hashMapOf("Time Remaining" to formatSeconds(machineState.timeRemaining))
                    ),
                    modifier = Modifier.weight(1f),
                    showAdditionalItems = showAdditionalDetailsForTime,
                    onShowAdditionalItemsClick = { showAdditionalDetailsForTime = !showAdditionalDetailsForTime }
                )
                KpiCard(
                    modifier = Modifier.weight(1f),
                    statItems = listOf(
                        hashMapOf("Heart Rate" to if (machineState.heartRate == 0) "--" else machineState.heartRate?.toString() ?: "--"),
                        hashMapOf("Peak Heart Rate" to machineState.maxHeartRate)
                    ),
                    showAdditionalItems = showAdditionalDetailsForHeartRate,
                    onShowAdditionalItemsClick = { showAdditionalDetailsForHeartRate = ! showAdditionalDetailsForHeartRate }
                )
                KpiCard(
                    modifier = Modifier.weight(1f),
                    statItems = listOf(
                        hashMapOf("Calories" to machineState.calories.toString()),
                        hashMapOf("Calories/Hour" to machineState.caloriesAnHour.toString()),
                        hashMapOf("Watts" to machineState.watts.toString())
                    ),
                    showAdditionalItems = showAdditionalDetailsForCalories,
                    onShowAdditionalItemsClick = { showAdditionalDetailsForCalories = !showAdditionalDetailsForCalories}
                )
            }


            ControlStepper(
                modifier = Modifier.weight(1f),
                heading = "Speed",
                unit = "MPH",
                value = machineState.instantaneousSpeed?.let {
                    String.format(
                        Locale.US,
                        "%.1f",
                        it
                    )
                } ?: "--"
            )
        }
    }
}

@TabletPreview
@Composable
fun TreadMillViewTabletPreview() {
    FitLinkTheme {
        TreadmillView(
            machineState = MachineUiState.TreadmillMachine(
                instantaneousSpeed = 500.0,
                heartRate = 100,
                inclination = 12.0,
                calories = "250",
                caloriesAnHour = "600",
                watts = "180",
                maxHeartRate = "170"
            ),
            connectionStatus = ConnectionStatus.Connected
        )
    }
}

@LandscapePhonePreview
@Composable
fun TreadMillViewPhoneLandscapeDarkPreview() {
    FitLinkTheme(darkTheme = true) {
        Scaffold { innerPadding ->
            TreadmillView(
                modifier = Modifier.padding(innerPadding),
                machineState = MachineUiState.TreadmillMachine(
                    instantaneousSpeed = 500.0,
                    heartRate = 100,
                    inclination = 12.0,
                    calories = "250",
                    caloriesAnHour = "600",
                    watts = "180",
                    maxHeartRate = "170"
                ),
                connectionStatus = ConnectionStatus.Connected
            )
        }
    }
}

@PortraitPhonePreview
@Composable
fun TreadMillViewPhonePortraitPreview() {
    FitLinkTheme {
        TreadmillView(
            machineState = MachineUiState.TreadmillMachine(
                instantaneousSpeed = 500.0,
                heartRate = 100,
                inclination = 12.0,
                calories = "250",
                caloriesAnHour = "600",
                watts = "180",
                maxHeartRate = "170"
            ),
            connectionStatus = ConnectionStatus.Connected
        )
    }
}