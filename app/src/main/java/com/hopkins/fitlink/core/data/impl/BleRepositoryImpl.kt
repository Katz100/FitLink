package com.hopkins.fitlink.core.data.impl

import android.os.ParcelUuid
import com.hopkins.fitlink.core.data.BleDevice
import com.hopkins.fitlink.core.data.BleRepository
import com.hopkins.fitlink.core.data.ConnectionStatus
import com.hopkins.fitlink.core.data.NotificationChanged
import com.hopkins.fitlink.core.data.toBleDevice
import com.hopkins.fitlink.core.ftms.EquipmentType
import com.hopkins.fitlink.core.ftms.FTMSConstants
import com.polidea.rxandroidble3.RxBleClient
import com.polidea.rxandroidble3.RxBleConnection
import com.polidea.rxandroidble3.helpers.ValueInterpreter
import com.polidea.rxandroidble3.scan.ScanFilter
import com.polidea.rxandroidble3.scan.ScanSettings
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.BiFunction
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
    private var activeConnection: RxBleConnection? = null
    private val operationDisposables = CompositeDisposable()

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

        val connection = activeConnection ?: run {
            Timber.tag(TAG).e("No active BLE connection")
            return
        }

        val disposable = connection.setupNotification(characteristic)
            .doOnNext {
                Timber.tag(TAG).i("Notification set up")
                onNotificationChanged(NotificationChanged.NotificationCreated)
            }
            .flatMap { stream ->
                stream
            }
            .doFinally {
                Timber.tag(TAG).i("Connection / notification stream ended")
                onNotificationChanged(NotificationChanged.NotificationEnded)
            }
            .subscribe(
                { bytes ->
                    val hex = bytes.joinToString(separator = " ") { byte ->
                        "%02X".format(byte.toInt() and 0xFF)
                    }

                    //   Timber.tag(TAG).i("Received bytes hex: $hex")
                    onBytesReceived(bytes)
                },
                { e ->
                    Timber.tag(TAG).e("Notification error: $e")
                    onNotificationChanged(NotificationChanged.NotificationError(e))
                }
            )
        operationDisposables.add(disposable)
    }

    override fun connectToDevice(
        deviceAddress: String,
        connectionStatusChanged: (ConnectionStatus) -> Unit
    ) {
        stopScanning()
        val device = rxBleClient.getBleDevice(deviceAddress)

        connectDisposable?.dispose()
        operationDisposables.clear()
        activeConnection = null

        connectDisposable = device.establishConnection(false)
            .doFinally {
                activeConnection = null
                connectDisposable = null
                Timber.tag(TAG).i("Disconnected to $device")
                connectionStatusChanged(ConnectionStatus.Disconnected)
            }
            .doOnNext {
                Timber.tag(TAG).i("Connected to $device")
                activeConnection = it
                connectionStatusChanged(ConnectionStatus.Connected)
            }
            .subscribe(
                {
                },
                {
                    activeConnection = null
                    Timber.tag(TAG).i("Error connecting to $deviceAddress: $it")
                    connectionStatusChanged(ConnectionStatus.ConnectionError(it))
                }
            )
    }

    override fun discoverCharacteristic(
        deviceAddress: String,
        onEquipmentCharacteristicFound: (EquipmentType) -> Unit,
        onFinished: () -> Unit,
    ) {
        val connection = activeConnection ?: run {
            Timber.tag(TAG).e("No active BLE connection")
            return
        }

        val ftmsServiceUuid = UUID.fromString(FTMSConstants.FTMS_MACHINE)

        val treadmillCharacteristicUuid = UUID.fromString(FTMSConstants.TREADMILL_CHARACTERISTIC)

        val disposable = connection
            .discoverServices()
            .map { services ->
                services.bluetoothGattServices
                    .firstOrNull { service ->
                        service.uuid == ftmsServiceUuid
                    }
                    ?.characteristics
                    .orEmpty()
            }
            .doFinally {
                Timber.tag(TAG).i("Characteristic discovery operation ended")
                onFinished()
            }
            .subscribe(
                { characteristics ->
                    characteristics.forEach { characteristic ->
                        when (characteristic.uuid) {
                            treadmillCharacteristicUuid -> {
                                Timber.tag(TAG).i("Found treadmill characteristic")
                                onEquipmentCharacteristicFound(
                                    EquipmentType.TREADMILL
                                )
                            }
                            else -> {
                                Timber.tag(TAG).i("Found: ${characteristic.uuid}")
                            }
                        }
                    }
                    Timber.tag(TAG).i("Finished processing characteristics")
                },
                { error ->
                    Timber.tag(TAG).e(error, "Error discovering characteristics")
                },
            )

        operationDisposables.add(disposable)
    }

    override fun writeToControlPoint() {
        val connection = activeConnection ?: run {
            Timber.tag(TAG).e("No active BLE connection")
            return
        }

        val controlPointUuid =
            UUID.fromString(FTMSConstants.FITNESS_MACHINE_CONTROL_POINT_UUID)

        val requestControlCommand = byteArrayOf(
            FTMSConstants.REQUEST_CONTROL_POINT.toByte()
        )

        val disposable = connection
            .setupIndication(controlPointUuid)
            .doOnNext {
                Timber.tag(TAG).i("Indication set up for control point")
            }
            .flatMapSingle { indications ->

                val responseSingle = indications
                    .filter { value ->
                        value.size >= 3 &&
                                value.uint8(0) == FTMSConstants.OP_RESPONSE_CODE &&
                                value.uint8(1) == FTMSConstants.REQUEST_CONTROL_POINT
                    }
                    .firstOrError()

                /*
                 * responseSingle is supplied first so the indication stream is
                 * subscribed before the write is initiated.
                 */
                Single.zip(
                    responseSingle,
                    connection.writeCharacteristic(
                        controlPointUuid,
                        requestControlCommand
                    ),
                    BiFunction<ByteArray, ByteArray, ByteArray> {
                            response, _ ->
                        response
                    }
                )
            }
            .firstOrError()
            .timeout(10, TimeUnit.SECONDS)
            .flatMapCompletable { response ->
                val resultCode = response.uint8(2)

                if (resultCode == FTMSConstants.RESULT_SUCCESS) {
                    Timber.tag(TAG).i("Control point granted")
                    Completable.complete()
                } else {
                    Completable.error(
                        RuntimeException("Control point failed ${resultCode}")
                    )
                }
            }
            .subscribe(
                {
                    Timber.tag(TAG).i("Request Control procedure completed")
                },
                { error ->
                    Timber.tag(TAG).e(
                        error,
                        "Request Control procedure failed"
                    )
                }
            )

        operationDisposables.add(disposable)
    }

    fun ByteArray.uint8(offset: Int): Int =
        requireNotNull(
            ValueInterpreter.getIntValue(
                this,
                ValueInterpreter.FORMAT_UINT8,
                offset
            )
        ) {
            "Unable to read UINT8 at offset $offset from $size-byte value"
        }

    override fun setSpeed(speedInKph: Double, deviceAddress: String) {
        val bytesToWrite = byteArrayOf(
            0x02.toByte(),
            (500 and 0xFF).toByte(),
            ((500 shr 8) and 0xFF).toByte()
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
        TODO("")
    }

    override fun connectToDevice(
        deviceAddress: String,
        on: (ConnectionStatus) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun discoverCharacteristic(
        deviceAddress: String,
        onEquipmentCharacteristicFound: (EquipmentType) -> Unit,
        onFinished: () -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun writeToControlPoint() {
        TODO("Not yet implemented")
    }

    override fun setSpeed(speedInKph: Double, deviceAddress: String) {
        TODO("Not yet implemented")
    }

    override fun isBleEnabled(): Boolean {
        return isBleOn
    }

}