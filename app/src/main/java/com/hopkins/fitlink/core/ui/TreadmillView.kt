package com.hopkins.fitlink.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import com.hopkins.fitlink.core.ftms.MachineState

@Composable
fun TreadmillView(
    machineState: MachineState.TreadmillMachine,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        ControlStepper(
            heading = "Speed",
            unit = "MPH",
            value = machineState.instantaneousSpeed.toString()
        )
        Text(machineState.heartRate.toString())
    }
}