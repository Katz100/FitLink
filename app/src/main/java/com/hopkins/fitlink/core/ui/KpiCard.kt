package com.hopkins.fitlink.core.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun KpiCard(
    modifier: Modifier = Modifier,
    statItems: List<HashMap<String, String>>,
    showAdditionalItems: Boolean,
    onShowAdditionalItemsClick: () -> Unit,
) {
    if (statItems.isEmpty()) return

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(
            space = 8.dp,
            alignment = Alignment.Bottom
        )
    ) {
        if (showAdditionalItems) {
            statItems.drop(1).forEach { item ->
                item.forEach { (key, value) ->
                    MetricCard(
                        label = key,
                        value = value
                    )
                }
            }
        }

        Box {
            for ((key, value) in statItems.first()) {
                MetricCard(
                    label = key,
                    value = value,
                    onClick = onShowAdditionalItemsClick
                )
            }

            Icon(
                modifier = Modifier.align(Alignment.TopEnd),
                imageVector = if (showAdditionalItems) {
                    Icons.Default.KeyboardArrowDown
                } else {
                    Icons.Default.KeyboardArrowUp
                },
                contentDescription = if (showAdditionalItems) {
                    "Hide additional metrics"
                } else {
                    "Show additional metrics"
                }
            )
        }
    }
}

@Composable
fun MetricCard(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    valueColor: Color = MaterialTheme.colorScheme.onSurface,
    labelColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    onClick: () -> Unit = {},
) {
    Card(
        modifier = modifier
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = valueColor
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                color = labelColor,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun KpiCardPreview() {
    val items = listOf(
        hashMapOf("Calories" to "0"),
        hashMapOf("Calories/Hour" to "99"),
        hashMapOf("Watts" to "23")
    )
    KpiCard(
        modifier = Modifier.width(200.dp),
        statItems = items,
        showAdditionalItems = true,
        onShowAdditionalItemsClick = {},
    )
}

@Preview()
@Composable
fun MetricCardPreview() {
    MetricCard(
        value = "99",
        label = "Calories/Hour",
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
    )
}