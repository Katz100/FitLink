package com.hopkins.fitlink.home

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.content.Context
import androidx.annotation.RequiresPermission
import androidx.lifecycle.ViewModel
import com.hopkins.fitlink.core.ble.Connectivity
import com.hopkins.fitlink.core.ble.FitBLE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val fitBLE: FitBLE
): ViewModel() {

    val devices: StateFlow<Set<BluetoothDevice>> = fitBLE.devices
    val isScanning: StateFlow<Boolean> = fitBLE.isScanning
    val connectivity: StateFlow<Connectivity> = fitBLE.connectivity

    fun scanForDevices(context: Context) {
        fitBLE.scanLeDevice(context)
    }

    fun clearDevices() {
        fitBLE.clearDevices()
    }

    fun isBleEnabled(): Boolean {
        return fitBLE.isBLESupported()
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun enableBle(context: Context) {
        fitBLE.enableBluetooth(context)
    }

    fun connectToDevice(
        context: Context,
        autoConnect: Boolean = true,
        device: BluetoothDevice,
    ) {
        fitBLE.connectToGATT(
            context = context,
            device = device,
            autoConnect = autoConnect
        )
    }
}