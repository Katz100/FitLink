package com.hopkins.fitlink.core.data

import com.polidea.rxandroidble3.RxBleDevice

data class BleDeviceModel(
    val name: String,
    val macAddress: String,
)

fun RxBleDevice.toBleDeviceModel(): BleDeviceModel {
    return BleDeviceModel(
        name = this.name ?: "N/A",
        macAddress = this.macAddress
    )
}