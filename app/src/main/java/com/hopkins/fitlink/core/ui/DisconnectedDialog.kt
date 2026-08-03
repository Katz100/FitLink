package com.hopkins.fitlink.core.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.TextButton

@Composable
fun DisconnectedDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
) {
    AlertDialog(
        icon = {
            Icon(Icons.Default.Clear, contentDescription = "Example Icon")
        },
        title = {
            Text(text = "Disconnected")
        },
        text = {
            Text(text = "The connection to the fitness equipment was lost." +
                    "Check that the equipment is powered on and nearby," +
                    "then try reconnecting.")
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text("Reconnect")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("End Workout")
            }
        }
    )
}