package com.hopkins.fitlink.core.data

import com.polidea.rxandroidble3.RxBleDevice
import java.util.UUID

interface BleRepository {
    fun scanDevices(
        onDeviceScanned: (RxBleDevice) -> Unit,
        onScanningFinished: () -> Unit,
    )

    fun connectAndSubscribeToCharacteristic(
        characteristic: UUID,
        device: RxBleDevice,
        onBytesReceived: (ByteArray) -> Unit,
        onNotificationCreated: () -> Unit,
        onNotificationEnded: () -> Unit,
        onNotificationError: (Throwable) -> Unit,
    )

    fun connectToDevice(
        device: RxBleDevice,
    )

    fun discoverCharacteristic(
        device: RxBleDevice,
        onEquipmentCharacteristicFound: (EQUIPMENT_TYPE) -> Unit,
        onFinished: () -> Unit,
    )
}

enum class EQUIPMENT_TYPE {
    TREADMILL,
}
