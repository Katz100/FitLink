package com.hopkins.fitlink.feature.home

import com.hopkins.fitlink.core.data.BleDeviceModel

data class HomeScreenUiState(
    val devices: List<BleDeviceModel> = emptyList(),
    val scannedResult: ScannedResult = ScannedResult.ScanningDevices,
)