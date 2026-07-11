package com.hopkins.fitlink.home

import android.bluetooth.BluetoothDevice
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    fun scanForDevices(context: Context) {
        fitBLE.scanLeDevice(context)
    }

    fun clearDevices() {
        fitBLE.clearDevices()
    }
}