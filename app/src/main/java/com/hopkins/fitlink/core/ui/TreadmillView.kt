package com.hopkins.fitlink.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            ControlStepper(
                modifier = Modifier.width(180.dp),
                heading = "Inclination",
                unit = "%",
                value = machineState.inclination?.toString() ?: "--"
            )

            Row {
                MachineDataCard(
                    modifier = Modifier.size(175.dp),
                    title = "Elapsed Time",
                    metric = "Minutes",
                    data = formatSeconds(machineState.elapsedTime)
                )
                Spacer(modifier = modifier.width(12.dp))
                MachineDataCard(
                    modifier = Modifier.size(175.dp),
                    title = "Remaining Time",
                    metric = "Minutes",
                    data = formatSeconds(machineState.timeRemaining)
                )
            }


            ControlStepper(
                modifier = Modifier.width(180.dp),
                heading = "Speed",
                unit = "MPH",
                value = String.format(
                    Locale.US,
                    "%.1f",
                    machineState.instantaneousSpeed
                )
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
                inclination = 12.0
            ),
            connectionStatus = ConnectionStatus.Connected
        )
    }
}

@LandscapePhoneDarkPreview
@Composable
fun TreadMillViewPhoneLandscapeDarkPreview() {
    FitLinkTheme(darkTheme = true) {
        Scaffold { innerPadding ->
            TreadmillView(
                modifier = Modifier.padding(innerPadding),
                machineState = MachineUiState.TreadmillMachine(
                    instantaneousSpeed = 500.0,
                    heartRate = 100,
                    inclination = 12.0
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
                inclination = 12.0
            ),
            connectionStatus = ConnectionStatus.Connected
        )
    }
}