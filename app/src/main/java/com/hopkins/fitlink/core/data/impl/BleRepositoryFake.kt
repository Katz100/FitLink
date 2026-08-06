package com.hopkins.fitlink.core.data.impl

import android.content.Context
import com.hopkins.fitlink.core.data.BleDeviceModel
import com.hopkins.fitlink.core.data.BleRepository
import com.hopkins.fitlink.core.data.ConnectionStatus
import com.hopkins.fitlink.core.data.NotificationChanged
import com.hopkins.fitlink.core.ftms.FTMSConstants
import com.hopkins.fitlink.core.ftms.domain.model.EquipmentType
import com.polidea.rxandroidble3.RxBleConnection
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import kotlin.math.roundToInt

class BleRepositoryFake @Inject constructor(
    @ApplicationContext context: Context,
) : BleRepository {

    companion object {
        private const val deviceName = "TestDevice"
        private const val macAddress = "AA:BB:CC:DD:EE:FF"
        private const val rrsi = -42
        private val serviceUuid = UUID.fromString(FTMSConstants.FTMS_MACHINE)
        private val characteristicUuid = UUID.fromString(FTMSConstants.TREADMILL_CHARACTERISTIC)
    }

    val scope = CoroutineScope(Dispatchers.Main)

    fun speedPacket(speedMph: Double): ByteArray {
        val speedKphHundredths = (speedMph * 1.609344 * 100).roundToInt()

        val speedLow = (speedKphHundredths and 0xFF).toByte()
        val speedHigh = ((speedKphHundredths shr 8) and 0xFF).toByte()

        return byteArrayOf(
            0x00,
            0x1F,
            speedLow,
            speedHigh,
            0x00,
            0x03,
            0x26,
            0x00,
            0xE2.toByte(),
            0x06,
            0xFF.toByte(),
            0x7F,
            0x31,
            0x00
        )
    }

override fun scanDevices(
    onDeviceScanned: (BleDeviceModel) -> Unit,
    onScanningFinished: () -> Unit
) {
    val device = BleDeviceModel(name = deviceName, macAddress = macAddress)
    onDeviceScanned(device)
    onScanningFinished
}

override fun subscribeToCharacteristic(
    characteristic: UUID,
    deviceAddress: String,
    onBytesReceived: (ByteArray) -> Unit,
    onNotificationChanged: (NotificationChanged) -> Unit
) {
    val speedFlow = flowOf(speedPacket(2.5), speedPacket(2.6), speedPacket(2.7), speedPacket(2.8))
    onNotificationChanged(NotificationChanged.NotificationCreated)
    scope.launch {
        speedFlow.collect { bytes ->
            onBytesReceived(bytes)
            delay(1_000)
        }
    }
}

override fun connectToDevice(
    deviceAddress: String,
    connectionStatusChanged: (ConnectionStatus) -> Unit
) {
    connectionStatusChanged(ConnectionStatus.Connected)
}

override fun disconnectFromDevice() {

}

override fun discoverCharacteristic(
    deviceAddress: String,
    onEquipmentCharacteristicFound: (EquipmentType) -> Unit,
    onFinished: () -> Unit
) {
    onEquipmentCharacteristicFound(EquipmentType.TREADMILL)
    onFinished()
}

override fun subscribeToConnectionState(
    deviceAddress: String,
    onConnectionStateChange: (RxBleConnection.RxBleConnectionState) -> Unit
) {
}

override fun writeToControlPoint() {
}

override fun setSpeed(speedInKph: Double, deviceAddress: String) {
    TODO("Not yet implemented")
}

override fun isBleEnabled(): Boolean {
    return true
}

override fun stopScanning() {

}

}