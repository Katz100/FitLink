package com.hopkins.fitlink.core.data

import com.polidea.rxandroidble3.RxBleDevice

data class BleDevice(
    val name: String,
    val macAddress: String,
)

fun RxBleDevice.toBleDevice(): BleDevice {
    return BleDevice(
        name = this.name ?: "N/A",
        macAddress = this.macAddress
    )
}