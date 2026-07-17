package com.hopkins.fitlink.core.data.impl

import android.os.ParcelUuid
import com.hopkins.fitlink.core.data.BleDevice
import com.hopkins.fitlink.core.data.BleRepository
import com.hopkins.fitlink.core.data.NotificationChanged
import com.hopkins.fitlink.core.data.toBleDevice
import com.hopkins.fitlink.core.ftms.EquipmentType
import com.hopkins.fitlink.core.ftms.FTMSConstants
import com.polidea.rxandroidble3.RxBleClient
import com.polidea.rxandroidble3.RxBleDevice
import com.polidea.rxandroidble3.scan.ScanFilter
import com.polidea.rxandroidble3.scan.ScanSettings
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class BleRepositoryImpl @Inject constructor(
    private val rxBleClient: RxBleClient
) : BleRepository {
    companion object {
        const val TAG = "BleRepository"
        const val TIMEOUT = 10L
    }

    private var scanDisposable: Disposable? = null
    private var connectDisposable: Disposable? = null

    override fun scanDevices(
        onDeviceScanned: (BleDevice) -> Unit,
        onScanningFinished: () -> Unit,
    ) {
        scanDisposable?.dispose()

        val scanSettings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
            .build()

        val parcelUuid: ParcelUuid = ParcelUuid.fromString(FTMSConstants.FTMS_MACHINE)

        val scanFilter = ScanFilter.Builder()
            .setServiceUuid(parcelUuid)
            .build()


        scanDisposable = rxBleClient
            .scanBleDevices(scanSettings, scanFilter)
            .take(TIMEOUT, TimeUnit.SECONDS)
            .doFinally {
                scanDisposable = null
                Timber.tag(TAG).i("Scanning has stopped")
                onScanningFinished()
            }
            .subscribe(
                { scanResult ->
                    onDeviceScanned(scanResult.bleDevice.toBleDevice())
                },
                { throwable ->
                    Timber.tag(TAG).e(throwable, "There was an error scanning devices")
                }
            )
    }

    override fun connectAndSubscribeToCharacteristic(
        characteristic: UUID,
        deviceAddress: String,
        onBytesReceived: (ByteArray) -> Unit,
        onNotificationChanged: (NotificationChanged) -> Unit,
    ) {
        stopScanning()
        connectDisposable?.dispose()
        val device = rxBleClient.getBleDevice(deviceAddress)

        connectDisposable = device.establishConnection(false)
            .flatMap { connection ->
                connection.setupNotification(characteristic)
            }
            .doOnNext {
                Timber.tag(TAG).i("Notification set up")
                onNotificationChanged(NotificationChanged.NotificationCreated)
            }
            .flatMap { stream ->
                stream
            }
            .doFinally {
                connectDisposable = null
                Timber.tag(TAG).i("Connection / notification stream ended")
                onNotificationChanged(NotificationChanged.NotificationEnded)
            }
            .subscribe(
                { bytes ->
                    val hex = bytes.joinToString(separator = " ") { byte ->
                        "%02X".format(byte.toInt() and 0xFF)
                    }

                    Timber.tag(TAG).i("Received bytes hex: $hex")
                    onBytesReceived(bytes)
                },
                { e ->
                    Timber.tag(TAG).e("Notification error: $e")
                    onNotificationChanged(NotificationChanged.NotificationError(e))
                }
            )
    }

    override fun connectToDevice(device: RxBleDevice) {
        TODO("Not yet implemented")
    }

    override fun discoverCharacteristic(
        deviceAddress: String,
        onEquipmentCharacteristicFound: (EquipmentType) -> Unit,
        onFinished: () -> Unit,
    ) {
        stopScanning()
        connectDisposable?.dispose()

        val device = rxBleClient.getBleDevice(deviceAddress)

        connectDisposable = device.establishConnection(false)
            .flatMapSingle { connection ->
                connection.discoverServices()
            }.map { services ->
                services.bluetoothGattServices.firstOrNull {
                    it.uuid == UUID.fromString(FTMSConstants.FTMS_MACHINE)
                }
                    ?.characteristics
                    ?: emptyList()
            }
            .doFinally {
                Timber.tag(TAG).i("Finished discovering characteristics")
                connectDisposable = null
                onFinished()
            }
            .subscribe(
                { characteristic ->
                    characteristic.forEach { ch ->
                        if (ch.uuid == UUID.fromString(FTMSConstants.TREADMILL_CHARACTERISTIC)) {
                            Timber.tag(TAG).i("Found Treadmill Characteristic")
                            onEquipmentCharacteristicFound(EquipmentType.TREADMILL)
                        } else {
                            Timber.tag(TAG).i("Found: ${ch.uuid}")
                        }
                    }
                    connectDisposable?.dispose()
                    connectDisposable = null
                },
                { t ->
                    Timber.tag(TAG).e("Error getting characteristics: $t")
                }
            )
    }

    override fun setSpeed(speedInKph: Double, deviceAddress: String) {
        val bytesToWrite = byteArrayOf(
            0x02.toByte(),
            (500 and 0xFF).toByte(),
            ((500 shr 8) and 0xFF).toByte()
        )
        writeToControlPoint(
            deviceAddress = deviceAddress,
            onConfirmed = {
                connectDisposable?.dispose()
                val device = rxBleClient.getBleDevice(deviceAddress)
                connectDisposable = device.establishConnection(false)
                    .flatMapSingle { connection ->
                        connection.writeCharacteristic(
                            UUID.fromString(FTMSConstants.FITNESS_MACHINE_CONTROL_POINT_UUID),
                            bytesToWrite
                        )
                    }
                    .subscribe(
                        { value ->
                            Timber.tag(TAG).i("Changed speed, received: $value")
                        },

                        { t ->
                            Timber.tag(TAG).e("Error changing speed: $t")
                        }
                    )
            },
            onError = {}
        )
    }

    private fun writeToControlPoint(
        deviceAddress: String,
        onConfirmed: (ByteArray) -> Unit,
        onError: () -> Unit,
    ) {
        val device = rxBleClient.getBleDevice(deviceAddress)

        val bytesToWrite = byteArrayOf(
            0x00.toByte()
        )
        val disposable: CompositeDisposable = CompositeDisposable()

        connectDisposable?.dispose()
        connectDisposable = device.establishConnection(false)
            .flatMapSingle { connection ->
                connection.writeCharacteristic(
                    UUID.fromString(FTMSConstants.FITNESS_MACHINE_CONTROL_POINT_UUID),
                    bytesToWrite
                )
            }
            .subscribe(
                { value ->
                    Timber.tag(TAG).i("Writing to CPUUID confirmed: $value")
                    if (value.contains((0x80 and 0xFF).toByte()) || value.contains(0x01.toByte())) {
                        onConfirmed(bytesToWrite)
                    } else {
                        Timber.tag(TAG).i("Value does not contain 0x80")
                    }
                },
                { t ->
                    Timber.tag(TAG).e("Error writing to CPUUID: $t")
                    onError()
                }
            )
    }

    override fun isBleEnabled(): Boolean {
        val state = rxBleClient.state
        return state != RxBleClient.State.BLUETOOTH_NOT_ENABLED
    }

    private fun stopScanning() {
        scanDisposable?.dispose()
        scanDisposable = null
    }
}

class BleRepositoryFake(
    private val scope: CoroutineScope
) : BleRepository {

    var isBleOn: Boolean = true
    private val deviceName = "TestDevice"
    private val macAddress = "AA:BB:CC:DD:EE:FF"


    override fun scanDevices(
        onDeviceScanned: (BleDevice) -> Unit,
        onScanningFinished: () -> Unit
    ) {
        val devices = flowOf(BleDevice(deviceName, macAddress))

        scope.launch {
            devices
                .onCompletion {
                    onScanningFinished()
                }
                .collect { device ->
                    onDeviceScanned(device)
                }
        }
    }

    override fun connectAndSubscribeToCharacteristic(
        characteristic: UUID,
        deviceAddress: String,
        onBytesReceived: (ByteArray) -> Unit,
        onNotificationChanged: (NotificationChanged) -> Unit
    ) {

    }

    override fun connectToDevice(device: RxBleDevice) {
        TODO("Not yet implemented")
    }

    override fun discoverCharacteristic(
        deviceAddress: String,
        onEquipmentCharacteristicFound: (EquipmentType) -> Unit,
        onFinished: () -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun setSpeed(speedInKph: Double, deviceAddress: String) {
        TODO("Not yet implemented")
    }

    override fun isBleEnabled(): Boolean {
        return isBleOn
    }

}