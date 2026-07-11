package com.hopkins.fitlink.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hopkins.fitlink.core.ui.DeviceItem

@Composable
fun HomeScreen() {
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(innerPadding)
        ) {
            DeviceItem(
                icon = Icons.Default.Settings,
                modifier = Modifier.fillMaxWidth()
                    .height(100.dp)
                    .padding(16.dp),
                deviceName = "CTM233132123",
                deviceAddress = "23:23:23:23:23",
                deviceNameTextStyle = MaterialTheme.typography.titleMedium,
                deviceAddressTextStyle = MaterialTheme.typography.bodySmall,
            )
        }
    }
}