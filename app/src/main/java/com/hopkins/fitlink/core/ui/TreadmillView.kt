package com.hopkins.fitlink.core.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.hopkins.fitlink.core.ftms.MachineState
import java.util.Locale

@Composable
fun TreadmillView(
    modifier: Modifier = Modifier,
    machineState: MachineState.TreadmillMachine,
    connectionStatus: ConnectionStatus,
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        ControlStepper(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .width(150.dp),
            heading = "Inclination",
            unit = "%",
            value = if (machineState.inclination == null) {
                "0.0"
            } else {
                machineState.inclination.toString()
            }
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

@Preview(showBackground = true)
@Composable
fun TreadMillViewPreview() {
    TreadmillView(
        modifier = Modifier.size(
            width = 500.dp,
            height = 200.dp
        ),
        machineState = MachineState.TreadmillMachine(
            instantaneousSpeed = 500.0,
            heartRate = 100,
            inclination = 12.0
        ),
        connectionStatus = ConnectionStatus.Connected
    )
}