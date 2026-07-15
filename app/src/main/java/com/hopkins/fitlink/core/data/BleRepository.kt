package com.hopkins.fitlink.core.data

import com.polidea.rxandroidble3.RxBleDevice
import java.util.UUID

interface BleRepository {
    /**
     * Scans for BLE devices.
     *
     * <p>Performs a scan for FTMS-supported devices. When a device is found,
     * {@code onDeviceScanned()} is called with the scanned device. After 10 seconds,
     * scanning is stopped and {@code onScanningFinished()} is called.</p>
     *
     * @param onDeviceScanned the lambda that is called when a device is scanned.
     *                        The device is passed as a parameter
     * @param onScanningFinished the lambda that is called when scanning is finished
     */
    fun scanDevices(
        onDeviceScanned: (RxBleDevice) -> Unit,
        onScanningFinished: () -> Unit,
    )

    /**
     * Connect and subscribe to a device's characteristic
     * @param characteristic The characteristic you want to subscribe to
     * @param device The device that contains the characteristic
     * @param onBytesReceived The lambda that is called when the characteristic is updated.
     */
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
