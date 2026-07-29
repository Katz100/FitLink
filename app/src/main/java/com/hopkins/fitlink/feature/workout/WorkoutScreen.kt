package com.hopkins.fitlink.feature.workout

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hopkins.fitlink.core.data.ConnectionStatus
import com.hopkins.fitlink.core.ftms.domain.model.MachineUiState
import com.hopkins.fitlink.core.ui.TreadmillView

@Composable
fun WorkoutScreen(
    viewModel: WorkoutScreenViewModel = hiltViewModel()
) {
    val uiState = viewModel.workoutUiState.collectAsStateWithLifecycle().value

    LaunchedEffect(uiState.connectionState) {
        if (uiState.connectionState == ConnectionStatus.Disconnected) {
            // show dialog
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        when (uiState.machineUiState) {
            MachineUiState.DetectingMachine -> Box(
                modifier = Modifier.fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            is MachineUiState.TreadmillMachine -> {
                TreadmillView(
                    modifier = Modifier.padding(innerPadding),
                    machineState = uiState.machineUiState,
                    connectionStatus = uiState.connectionState
                )
            }
        }
    }

}