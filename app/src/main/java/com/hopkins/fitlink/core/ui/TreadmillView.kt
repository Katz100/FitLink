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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hopkins.fitlink.feature.workout.MachineUiState
import com.hopkins.fitlink.core.ftms.util.formatSeconds
import com.hopkins.fitlink.feature.workout.FitnessMachineStatus
import com.hopkins.fitlink.ui.theme.FitLinkTheme
import java.util.Locale

@Composable
fun TreadmillView(
    modifier: Modifier = Modifier,
    machineState: MachineUiState.TreadmillMachine,
    machineStatus: FitnessMachineStatus,
    showAdditionalDetailsForTime: Boolean = false,
    showAdditionalDetailsForCalories: Boolean = false,
    showAdditionalDetailsForHeartRate: Boolean = false,
    onShowAdditionalDetailsForTimeClicked: () -> Unit = {},
    onShowAdditionalDetailsForCaloriesClicked: () -> Unit = {},
    onShowAdditionalDetailsForHeartRateClicked: () -> Unit = {},
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            modifier = Modifier.align(Alignment.TopCenter),
            text = machineStatus.toString(),
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
                    onShowAdditionalItemsClick = onShowAdditionalDetailsForTimeClicked
                )
                KpiCard(
                    modifier = Modifier.weight(1f),
                    statItems = listOf(
                        hashMapOf("Heart Rate" to if (machineState.heartRate == 0) "--" else machineState.heartRate?.toString() ?: "--"),
                        hashMapOf("Peak Heart Rate" to machineState.maxHeartRate)
                    ),
                    showAdditionalItems = showAdditionalDetailsForHeartRate,
                    onShowAdditionalItemsClick = onShowAdditionalDetailsForHeartRateClicked
                )
                KpiCard(
                    modifier = Modifier.weight(1f),
                    statItems = listOf(
                        hashMapOf("Calories" to machineState.calories.toString()),
                        hashMapOf("Calories/Hour" to machineState.caloriesAnHour.toString()),
                        hashMapOf("Watts" to machineState.watts.toString())
                    ),
                    showAdditionalItems = showAdditionalDetailsForCalories,
                    onShowAdditionalItemsClick = onShowAdditionalDetailsForCaloriesClicked
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
            machineStatus = FitnessMachineStatus.Started
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
                machineStatus = FitnessMachineStatus.Started,
                showAdditionalDetailsForCalories = true,
                showAdditionalDetailsForTime = true,
                showAdditionalDetailsForHeartRate = true,
            )
        }
    }
}

@LandscapePhoneIncreasedFontPreview
@Composable
fun TreadMillViewPhoneLandscapeDarkIncreasedFontPreview() {
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
                machineStatus = FitnessMachineStatus.Started,
                showAdditionalDetailsForCalories = true,
                showAdditionalDetailsForTime = true,
                showAdditionalDetailsForHeartRate = true,
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
            machineStatus = FitnessMachineStatus.Started
        )
    }
}