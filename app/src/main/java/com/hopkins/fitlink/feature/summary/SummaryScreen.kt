package com.hopkins.fitlink.feature.summary

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hopkins.fitlink.core.data.room.entity.TreadmillSessionDomain
import com.hopkins.fitlink.core.domain.model.EquipmentType
import com.hopkins.fitlink.core.ftms.util.formatSeconds
import com.hopkins.fitlink.core.ui.MetricSummary
import com.hopkins.fitlink.core.ui.WorkoutSummaryRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummaryScreen(
    viewModel: SummaryScreenViewModel = hiltViewModel(),
    onHomeButtonClicked: () -> Unit,
) {
    val uiState = viewModel.uiState.collectAsState().value

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                ),
                title = {
                    Text(
                        style = MaterialTheme.typography.headlineMedium,
                        text = "Results"
                    )
                },
                navigationIcon = {
                    IconButton(
                        modifier = Modifier.padding(start = 8.dp),
                        onClick = onHomeButtonClicked
                    ) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Home"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        when (uiState.result) {
            is WorkoutResult.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("There was an error loading this session.")
                }
            }
            is WorkoutResult.Success<*> -> {
                when (viewModel.equipmentType) {
                    EquipmentType.TREADMILL -> {
                        val treadmillSession = uiState.result.data as TreadmillSessionDomain

                        Box(
                            modifier = Modifier.padding(innerPadding)
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            WorkoutSummaryRow(
                                metrics = listOf(
                                    {
                                        MetricSummary(
                                            modifier = Modifier.weight(1f),
                                            valueTextStyle = MaterialTheme.typography.headlineMedium,
                                            heading = "Avg Speed",
                                            value = treadmillSession.avgSpeed.toString(),
                                            unit = "mph",
                                            icon = Icons.Default.Build
                                        )
                                    },
                                    {
                                        MetricSummary(
                                            modifier = Modifier.weight(1f),
                                            valueTextStyle = MaterialTheme.typography.headlineMedium,
                                            heading = "Total Calories",
                                            value = treadmillSession.calories.toString(),
                                            unit = "kcal",
                                            icon = Icons.Default.Build
                                        )
                                    },
                                    {
                                        MetricSummary(
                                            modifier = Modifier.weight(1f),
                                            valueTextStyle = MaterialTheme.typography.headlineMedium,
                                            heading = "Average Hr",
                                            value = treadmillSession.avgHr.toString(),
                                            unit = "bpm",
                                            icon = Icons.Default.Build
                                        )
                                    },
                                    {
                                        MetricSummary(
                                            modifier = Modifier.weight(1f),
                                            valueTextStyle = MaterialTheme.typography.headlineMedium,
                                            heading = "Total Duration",
                                            value = formatSeconds(treadmillSession.duration),
                                            unit = "minutes",
                                            icon = Icons.Default.Build
                                        )
                                    },
                                )
                            )
                        }
                    }

                    else -> {}
                }
            }
            is WorkoutResult.Loading -> {}
        }
    }
}