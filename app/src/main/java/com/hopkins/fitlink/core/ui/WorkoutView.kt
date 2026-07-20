package com.hopkins.fitlink.core.ui

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import com.hopkins.fitlink.core.ftms.MachineState

@Composable
fun WorkoutView(
    machineState: MachineState
) {
    when (machineState) {
        MachineState.DetectingMachine -> CircularProgressIndicator()
        is MachineState.TreadmillMachine -> TreadmillView(machineState)
    }
}