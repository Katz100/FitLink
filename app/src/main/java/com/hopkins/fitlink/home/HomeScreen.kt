package com.hopkins.fitlink.home

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hopkins.fitlink.core.ble.Connectivity
import com.hopkins.fitlink.core.ble.FitBLE
import com.hopkins.fitlink.core.ui.DeviceItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeScreenViewModel = hiltViewModel(),
    onDeviceClicked: (String) -> Unit,
) {
    val context = LocalContext.current
    val devices = viewModel.devices.collectAsStateWithLifecycle().value
    val isScanning = viewModel.scanning.collectAsStateWithLifecycle().value
    val connectivity = Connectivity.DISCONNECTED

    val bluetoothPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = !permissions.values.contains(false)
        if (allGranted) {
            Log.i("TAG", "Bluetooth permissions granted")
        } else {
            Log.i("TAG", "Bluetooth permissions denied: $permissions")
        }
    }

    LaunchedEffect(connectivity) {
        Toast.makeText(context, "Connectivity: ${connectivity.name}", Toast.LENGTH_SHORT).show()
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
                            if(!viewModel.isBleEnabled()) {
                                if (ContextCompat.checkSelfPermission(
                                        context, Manifest.permission.BLUETOOTH_CONNECT
                                ) == PackageManager.PERMISSION_GRANTED) {
                                    val enableBleIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                                    context.startActivity(enableBleIntent)
                                }
                            } else if (FitBLE.isBLEPermissionsGranted(context)) {
                                viewModel.scanForDevices()
                            } else {
                                bluetoothPermissionLauncher.launch(FitBLE.BLE_PERMISSIONS)
                            }
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            item {
                if (devices.isEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No devices available...",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }
            items(devices.toList(), key = { it.macAddress }) { device ->
                if (ContextCompat.checkSelfPermission(
                        context, Manifest.permission.BLUETOOTH_SCAN
                ) == PackageManager.PERMISSION_GRANTED) {
                    DeviceItem(
                        icon = Icons.Default.Settings,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .padding(16.dp),
                        deviceName = device.name ?: "N/A",
                        deviceAddress = device.macAddress,
                        deviceNameTextStyle = MaterialTheme.typography.titleMedium,
                        deviceAddressTextStyle = MaterialTheme.typography.bodySmall,
                        onConnectClicked = {
                            onDeviceClicked(device.macAddress)
                        }
                    )
                }
            }
        }
    }
}