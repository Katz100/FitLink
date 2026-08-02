package com.hopkins.fitlink.core.data

import com.hopkins.fitlink.core.ftms.domain.model.EquipmentType
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
        onDeviceScanned: (BleDeviceModel) -> Unit,
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
    fun subscribeToCharacteristic(
        characteristic: UUID,
        deviceAddress: String,
        onBytesReceived: (ByteArray) -> Unit,
        onNotificationChanged: (NotificationChanged) -> Unit,
    )

    fun connectToDevice(
        deviceAddress: String,
        connectionStatusChanged: (ConnectionStatus) -> Unit,
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

    fun writeToControlPoint()

    fun setSpeed(
        speedInKph: Double,
        deviceAddress: String
    )

    fun isBleEnabled(): Boolean
}

