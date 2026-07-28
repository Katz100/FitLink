package com.hopkins.fitlink.core.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hopkins.fitlink.core.data.ConnectionStatus
import com.hopkins.fitlink.core.ftms.domain.model.MachineUiState
import com.hopkins.fitlink.ui.theme.FitLinkTheme
import java.util.Locale

@Composable
fun TreadmillView(
    modifier: Modifier = Modifier,
    machineState: MachineUiState.TreadmillMachine,
    connectionStatus: ConnectionStatus,
) {
    Box(
        modifier = modifier.fillMaxSize()
            .padding(16.dp)
    ) {
        ControlStepper(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .width(180.dp),
            heading = "Inclination",
            unit = "%",
            value = if (machineState.inclination == null) "--" else machineState.inclination.toString()
        )

        Text(
            modifier = Modifier.align(Alignment.Center),
            text = connectionStatus.toString(),
            color = when(connectionStatus) {
                ConnectionStatus.Connected -> Color.Green
                is ConnectionStatus.ConnectionError -> Color.Red
                ConnectionStatus.ConnectionLoading -> Color.Black
                ConnectionStatus.Disconnected -> Color.Yellow
            }
        )

        ControlStepper(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .width(150.dp),
            heading = "Speed",
            unit = "MPH",
            value = String.format(Locale.US,"%.1f", machineState.instantaneousSpeed)

        )
    }
}

@Preview(showBackground = true, device = "spec:width=1280dp,height=800dp,dpi=240")
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

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp,dpi=420,isRound=false,chinSize=0dp,orientation=landscape,cutout=none,navigation=gesture")
@Composable
fun TreadMillViewPhoneLandscapePreview() {
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

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp,dpi=420,isRound=false,chinSize=0dp,orientation=portrait,cutout=none,navigation=gesture")
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