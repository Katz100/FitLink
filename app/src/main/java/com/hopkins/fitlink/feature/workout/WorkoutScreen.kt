package com.hopkins.fitlink.feature.workout

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hopkins.fitlink.core.data.ConnectionStatus
import com.hopkins.fitlink.core.ftms.domain.model.MachineUiState
import com.hopkins.fitlink.core.ui.DisconnectedDialog
import com.hopkins.fitlink.core.ui.TreadmillView

@Composable
fun WorkoutScreen(
    viewModel: WorkoutScreenViewModel = hiltViewModel(),
    onWorkoutEnded: () -> Unit,
) {
    val uiState = viewModel.workoutUiState.collectAsStateWithLifecycle().value
    var showDisconnectedDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(uiState.connectionState) {
        when (uiState.connectionState) {
            ConnectionStatus.Connected -> showDisconnectedDialog = false
            is ConnectionStatus.ConnectionError -> Unit
            ConnectionStatus.ConnectionLoading -> Unit
            ConnectionStatus.Disconnected -> showDisconnectedDialog = true
        }
    }

    LaunchedEffect(uiState.notificationStatus) {
        Toast.makeText(context, uiState.notificationStatus.toString(), Toast.LENGTH_SHORT).show()
    }

    LaunchedEffect(uiState.rxConnectionState) {
        Toast.makeText(context, uiState.rxConnectionState.toString(), Toast.LENGTH_SHORT).show()
    }

    BackHandler {
        viewModel.disconnectFromDevice()
        onWorkoutEnded()
    }

    if (showDisconnectedDialog) {
        DisconnectedDialog(
            onConfirmation = {
                viewModel.connectToDevice()
                showDisconnectedDialog = false
            },
            onDismissRequest = {
                showDisconnectedDialog = false
                onWorkoutEnded()
            },
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->

        val isLoading = uiState.connectionState == ConnectionStatus.ConnectionLoading

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Connecting to machine...",
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            when (val machineState = uiState.machineUiState) {
                is MachineUiState.TreadmillMachine -> {
                    TreadmillView(
                        modifier = Modifier.padding(innerPadding),
                        machineState = machineState,
                        machineStatus = uiState.fitnessMachineStatus
                    )
                }

                MachineUiState.DetectingMachine -> {
                    // TODO: create reusable composable for custom circular progress indicator
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator()

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Identifying machine type...",
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }

}