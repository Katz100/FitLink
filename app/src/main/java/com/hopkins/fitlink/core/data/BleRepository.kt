package com.hopkins.fitlink.core.data

import com.hopkins.fitlink.core.ftms.EquipmentType
import com.polidea.rxandroidble3.RxBleDevice
import java.util.UUID

interface BleRepository {
    /**
     * Scans for BLE devices that support FTMS.
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
        onDeviceScanned: (BleDevice) -> Unit,
        onScanningFinished: () -> Unit,
    )

    /**
     * Connect and subscribe to a device's characteristic
     * @param characteristic The characteristic you want to subscribe to
     * @param deviceAddress The device that contains the characteristic
     * @param onBytesReceived The lambda that is called when the characteristic is updated.
     * @param onNotificationChanged The lambda that is called when there is a change in the notification.
     * Passes a NotificationChanged object as a parameter
     */
    fun connectAndSubscribeToCharacteristic(
        characteristic: UUID,
        deviceAddress: String,
        onBytesReceived: (ByteArray) -> Unit,
        onNotificationChanged: (NotificationChanged) -> Unit,
    )

    fun connectToDevice(
        device: RxBleDevice,
    )

    /**
     * Discovers characteristics for a device
     * @param deviceAddress The device you want to find characteristics for
     * @param onEquipmentCharacteristicFound A lambda that is called when a characteristic is found
     * @param onFinished A lambda that is called when every characteristic is found for FTMS-supported device
     */
    fun discoverCharacteristic(
        deviceAddress: String,
        onEquipmentCharacteristicFound: (EquipmentType) -> Unit,
        onFinished: () -> Unit,
    )

    fun isBleEnabled(): Boolean
}

sealed interface NotificationChanged {
    data object NotificationCreated: NotificationChanged
    data object NotificationEnded: NotificationChanged
    data class NotificationError(val e: Throwable): NotificationChanged
}

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