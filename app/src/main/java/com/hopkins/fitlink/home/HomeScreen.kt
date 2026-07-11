package com.hopkins.fitlink.home

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeScreenViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val devices = viewModel.devices.collectAsStateWithLifecycle().value
    val isScanning = viewModel.isScanning.collectAsStateWithLifecycle().value

    LaunchedEffect(devices) {
        devices.forEach { device ->
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, device.name, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "BLE permission not granted", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text("Connect to your machine")
                },
                actions = {
                    Button(
                        onClick = {
                            viewModel.scanForDevices(context)
                        },
                        enabled = !isScanning,
                        shape = ShapeDefaults.Medium,
                    ) {
                        if (isScanning) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text("SCAN")
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
//            DeviceItem(
//                icon = Icons.Default.Settings,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(100.dp)
//                    .padding(16.dp),
//                deviceName = "CTM233132123",
//                deviceAddress = "23:23:23:23:23",
//                deviceNameTextStyle = MaterialTheme.typography.titleMedium,
//                deviceAddressTextStyle = MaterialTheme.typography.bodySmall,
//            )
        }
    }
}