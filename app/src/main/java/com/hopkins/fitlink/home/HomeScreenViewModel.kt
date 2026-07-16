package com.hopkins.fitlink.home

import androidx.lifecycle.ViewModel
import com.hopkins.fitlink.core.data.BleDevice
import com.hopkins.fitlink.core.data.BleRepository
import com.polidea.rxandroidble3.RxBleDevice
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val bleRepository: BleRepository,
) : ViewModel() {

    private val _devices = MutableStateFlow<List<BleDevice>>(emptyList())
    val devices: StateFlow<List<BleDevice>> = _devices.asStateFlow()

    private val _scanning = MutableStateFlow<Boolean>(false)
    val scanning: StateFlow<Boolean> = _scanning.asStateFlow()

    fun scanForDevices() {
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
        _devices.value = emptyList()
    }

    fun isBleEnabled(): Boolean {
        return bleRepository.isBleEnabled()
    }

}
