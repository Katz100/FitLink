package com.hopkins.fitlink.core.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun DeviceItem(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    deviceName: String = "N/A",
    deviceAddress: String,
    deviceNameTextStyle: TextStyle,
    deviceAddressTextStyle: TextStyle,
    onConnectClicked: () -> Unit,
) {
    Card(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Icon(
                modifier = Modifier
                    .padding(start = 8.dp, top = 8.dp),
                imageVector = icon,
                contentDescription = "Machine Icon"
            )
            Column(modifier = Modifier
                .padding(start = 8.dp)
            ) {
                Text(
                    text = deviceName,
                    style = deviceNameTextStyle
                )
                Text(
                    text = deviceAddress,
                    style = deviceAddressTextStyle
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = onConnectClicked
            ) {
                Text(
                    text = "CONNECT"
                )
            }
        }
    }
}

@Preview
@Composable
fun DeviceItemPreview() {
    DeviceItem(
        modifier = Modifier
            .size(width = 400.dp, height = 100.dp)
            .padding(15.dp),
        icon = Icons.Default.Build,
        deviceName = "CTM34232342",
        deviceAddress = "23:23:23:23:23:23",
        deviceNameTextStyle = MaterialTheme.typography.titleMedium,
        deviceAddressTextStyle = MaterialTheme.typography.bodySmall,
        onConnectClicked = {}
    )
}