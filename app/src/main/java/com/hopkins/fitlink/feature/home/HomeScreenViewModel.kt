package com.hopkins.fitlink.feature.home

import androidx.lifecycle.ViewModel
import com.hopkins.fitlink.core.domain.model.BleDeviceModel
import com.hopkins.fitlink.core.data.repository.BleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val bleRepository: BleRepository,
) : ViewModel() {

    private val _devices = MutableStateFlow<List<BleDeviceModel>>(emptyList())
    val devices: StateFlow<List<BleDeviceModel>> = _devices.asStateFlow()

    private val _scanning = MutableStateFlow<Boolean>(false)
    val scanning: StateFlow<Boolean> = _scanning.asStateFlow()

    private val _homeUiState = MutableStateFlow<HomeScreenUiState>(HomeScreenUiState())
    val homeScreenUiState: StateFlow<HomeScreenUiState> = _homeUiState.asStateFlow()

    init {
        scanForDevices()
    }

    fun scanForDevices() {
        _scanning.value = true
        _homeUiState.update {
            it.copy(
                scannedResult = ScannedResult.ScanningDevices
            )
        }
        clearDevices()
        bleRepository.scanDevices(
            onDeviceScanned = { device ->
                if (!_devices.value.contains(device)) {
                    _devices.value = _devices.value + device
                }
            },
            onScanningFinished = {
                _homeUiState.update {
                    it.copy(
                        scannedResult = ScannedResult.ScanNotInProgress
                    )
                }
                _scanning.value = false
                if (_devices.value.isEmpty()) {
                    _homeUiState.update {
                        it.copy(
                            scannedResult = ScannedResult.NoDevicesFound
                        )
                    }
                }
            }
        )
    }

    fun clearDevices() {
        _devices.value = emptyList()
    }

    fun isBleEnabled(): Boolean {
        return bleRepository.isBleEnabled()
    }

    fun stopScanning() {
        bleRepository.stopScanning()
    }
}

sealed interface ScannedResult {
    data object ScanningDevices: ScannedResult
    data object ScanNotInProgress: ScannedResult
    data object NoDevicesFound: ScannedResult
}
