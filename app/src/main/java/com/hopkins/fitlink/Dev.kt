package com.hopkins.fitlink

import android.util.Log
import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@Composable
fun Dev(
    onRequestPermission: () -> Unit,
    fitBLE: FitBLE,
) {
    val context = LocalContext.current

    val bluetoothPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = !permissions.values.contains(false)

        if (allGranted) {
            Log.i("TAG", "Bluetooth permissions granted")
            onRequestPermission()
        } else {
            Log.i("TAG", "Bluetooth permissions denied: $permissions")
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Button(onClick = {
                val permissions = arrayOf(
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                )
                if (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.BLUETOOTH_CONNECT,
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    Log.i("TAG", "Permission already granted")
                } else {
                    bluetoothPermissionLauncher.launch(permissions)
                }
            }
            ) {
                Text("Request Permissions")
            }
            Button(
                onClick = {
                    fitBLE.enableBluetooth(context)
                }
            ) {
                Text("Enable BLE")
            }
            Button(onClick = {
                val permissionsGranted = FitBLE.isBLEPermissionsGranted(context)
                Toast.makeText(context, permissionsGranted.toString(), Toast.LENGTH_SHORT).show()
            }) {
                Text("BLE Permissions Granted?")
            }
            Button(onClick = {
                val isSupported = fitBLE.isBLESupported()
                Toast.makeText(context, isSupported.toString(), Toast.LENGTH_SHORT).show()

            }) {
                Text("BLE Supported?")
            }
            Button(onClick = {
                fitBLE.scanLeDevice()
            }) {
                Text("Start Scan")
            }
        }
    }
}