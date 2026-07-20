package com.hopkins.fitlink.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ControlStepper(
    modifier: Modifier = Modifier,
    heading: String,
    value: String,
    unit: String,
    buttonsEnabled: Boolean = true,
    onIncrementPressed: () -> Unit = {},
    onDecrementPressed: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceContainerLowest,
                shape = MaterialTheme.shapes.medium
            )
            .padding(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
            )
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(heading)
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(unit)
            }
        }

        StepperButton(
            modifier = Modifier.fillMaxWidth()
                .shadow(
                    elevation = 6.dp,
                    shape = MaterialTheme.shapes.medium,
                    clip = false
                ),
            enabled = buttonsEnabled,
            shape = MaterialTheme.shapes.medium,
            imageVector = Icons.Default.KeyboardArrowUp,
            onPressed = onIncrementPressed
        )

        StepperButton(
            modifier = Modifier.fillMaxWidth()
                .shadow(
                    elevation = 6.dp,
                    shape = MaterialTheme.shapes.medium,
                    clip = false
                ),
            enabled = buttonsEnabled,
            shape = MaterialTheme.shapes.medium,
            imageVector = Icons.Default.KeyboardArrowDown,
            onPressed = onDecrementPressed
        )
    }
}

@Composable
private fun StepperButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape,
    imageVector: ImageVector,
    onPressed: () -> Unit = {}
) {
    FilledIconButton(
        modifier = modifier,
        enabled = enabled,
        colors = IconButtonDefaults.iconButtonColors().copy(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        ),
        shape = shape,
        onClick = onPressed
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = "Step icon",
            tint = Color.Blue
        )
    }
}

@Preview(showBackground = true,)
@Composable
fun ControlStepperPreview() {
    ControlStepper(
        modifier = Modifier.width(150.dp),
        heading = "Speed",
        value = "0.5",
        unit = "MPH"
    )
}