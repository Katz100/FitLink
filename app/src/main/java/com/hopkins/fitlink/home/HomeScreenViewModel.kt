package com.hopkins.fitlink.home

import android.Manifest
import android.content.Context
import androidx.annotation.RequiresPermission
import androidx.lifecycle.ViewModel
import com.hopkins.fitlink.core.data.BleRepository
import com.hopkins.fitlink.core.ftms.FTMSConstants
import com.polidea.rxandroidble3.RxBleDevice
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val bleRepository: BleRepository,
    @ApplicationContext private val context: Context,
) : ViewModel() {

    private val _devices = MutableStateFlow<List<RxBleDevice>>(emptyList())
    val devices: StateFlow<List<RxBleDevice>> = _devices.asStateFlow()

    private val _scanning = MutableStateFlow<Boolean>(false)
    val scanning: StateFlow<Boolean> = _scanning.asStateFlow()

    // val connectivity: StateFlow<Connectivity> = fitBLE.connectivity

    fun scanForDevices(context: Context) {
        _scanning.value = true
        bleRepository.scanDevices(
            onDeviceScanned = { device ->
                if (!_devices.value.contains(device)) {
                    _devices.value = _devices.value + device
                }
            },
            onScanningFinished = {
                _scanning.value = false
            }
        )
    }

    fun clearDevices() {
    }

    fun isBleEnabled(): Boolean {
        return true
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun enableBle(context: Context) {
    }

    fun connectToDevice(
        device: RxBleDevice
    ) {
        bleRepository.connectAndSubscribeToCharacteristic(
            device = device,
            characteristic = UUID.fromString(
                FTMSConstants.TREADMILL_CHARACTERISTIC
            ),
            onNotificationEnded = {
            },
            onNotificationError = {
            },
            onNotificationCreated = {
            },
            onBytesReceived = {}
        )
    }
}
