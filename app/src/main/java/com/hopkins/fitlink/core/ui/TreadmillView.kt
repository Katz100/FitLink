package com.hopkins.fitlink.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hopkins.fitlink.core.ftms.MachineState

@Composable
fun TreadmillView(
    machineState: MachineState.TreadmillMachine,
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ControlStepper(
            modifier = Modifier.width(150.dp),
            heading = "Speed",
            unit = "MPH",
            value = machineState.instantaneousSpeed.toString()
        )
        Text(machineState.heartRate.toString())
    }
}