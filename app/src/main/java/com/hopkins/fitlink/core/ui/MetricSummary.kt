package com.hopkins.fitlink.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun MetricSummary(
    modifier: Modifier = Modifier,
    heading: String,
    value: String,
    unit: String,
    valueTextStyle: TextStyle,
    headingTextStyle: TextStyle = MaterialTheme.typography.labelMedium,
    unitTextStyle: TextStyle = MaterialTheme.typography.labelMedium,
    icon: ImageVector? = null,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            icon?.let {
                Icon(
                    imageVector = icon,
                    contentDescription = "Metric icon"
                )
            }
            Text(
                text = heading,
                style = headingTextStyle
            )
        }
        Text(
            text = value,
            style = valueTextStyle
        )
        Text(
            text = unit,
            style = unitTextStyle
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MetricSummaryPreview() {
    MetricSummary(
        valueTextStyle = MaterialTheme.typography.headlineMedium,
        modifier = Modifier.fillMaxSize(),
        heading = "Avg Speed",
        value = "0.5",
        unit = "mph",
        icon = Icons.Default.Build
    )
}