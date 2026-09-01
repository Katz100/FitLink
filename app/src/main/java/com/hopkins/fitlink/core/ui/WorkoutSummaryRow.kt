package com.hopkins.fitlink.core.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun WorkoutSummaryRow(
    modifier: Modifier = Modifier,
    metrics: List<@Composable RowScope.() -> Unit>
) {
    Row(
        modifier = modifier
    ) {
        metrics.forEach { metric ->
            metric()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WorkoutSummaryRowPreview() {
    WorkoutSummaryRow(
        modifier = Modifier.fillMaxWidth(),
        metrics = listOf(
            {
                MetricSummary(
                    valueTextStyle = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.weight(1f),
                    heading = "Avg Speed",
                    value = "0.5",
                    unit = "mph",
                    icon = Icons.Default.Build
                )
            },
            {
                MetricSummary(
                    valueTextStyle = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.weight(1f),
                    heading = "Avg Speed",
                    value = "0.5",
                    unit = "mph",
                    icon = Icons.Default.Build
                )
            },
            {
                MetricSummary(
                    valueTextStyle = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.weight(1f),
                    heading = "Avg Speed",
                    value = "0.5",
                    unit = "mph",
                    icon = Icons.Default.Build
                )
            },
        )
    )
}