package com.hopkins.fitlink.core.data

import com.polidea.rxandroidble3.RxBleDevice

interface BleRepository {
    fun scanDevices(
        onDeviceScanned: (RxBleDevice) -> Unit,
        onScanningFinished: () -> Unit,
    )

    fun connectToDevice(device: RxBleDevice)
}