package com.hopkins.fitlink.core.data.repository.impl

import android.os.ParcelUuid
import com.hopkins.fitlink.core.domain.model.BleDeviceModel
import com.hopkins.fitlink.core.data.repository.BleRepository
import com.hopkins.fitlink.core.domain.model.ConnectionStatus
import com.hopkins.fitlink.core.domain.model.NotificationChanged
import com.hopkins.fitlink.core.domain.model.toBleDeviceModel
import com.hopkins.fitlink.core.ftms.FTMSConstants
import com.hopkins.fitlink.core.domain.model.EquipmentType
import com.polidea.rxandroidble3.RxBleClient
import com.polidea.rxandroidble3.RxBleConnection
import com.polidea.rxandroidble3.scan.ScanFilter
import com.polidea.rxandroidble3.scan.ScanSettings
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
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
        onDeviceScanned: (BleDeviceModel) -> Unit,
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
                    onDeviceScanned(scanResult.bleDevice.toBleDeviceModel())
                },
                { throwable ->
                    Timber.tag(TAG).e(throwable, "There was an error scanning devices")
                }
            )
    }

    override fun subscribeToCharacteristic(
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
        val device = rxBleClient.getBleDevice(deviceAddress)

        connectDisposable?.dispose()
        connectDisposable = null

        operationDisposables.clear()
        activeConnection = null

        connectDisposable = device.establishConnection(false)
            .doFinally {
                activeConnection = null
                connectDisposable = null
                operationDisposables.clear()
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

    override fun disconnectFromDevice() {
        val connectionDisposable = connectDisposable

        if (connectionDisposable == null || connectionDisposable.isDisposed) {
            Timber.tag(TAG).i("No active BLE connection to disconnect")

            operationDisposables.clear()
            activeConnection = null
            connectDisposable = null
            return
        }

        Timber.tag(TAG).i("Disconnecting active BLE connection")
        operationDisposables.clear()
        connectionDisposable.dispose()
        connectDisposable = null
        activeConnection = null
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

    override fun subscribeToConnectionState(
        deviceAddress: String,
        onConnectionStateChange: (RxBleConnection.RxBleConnectionState) -> Unit,
    ) {
        val device = rxBleClient.getBleDevice(deviceAddress)

        val disposable = device.observeConnectionStateChanges()
            .subscribe (
                { connectionState ->
                    onConnectionStateChange(connectionState)
                },

                { t ->
                    Timber.tag(TAG).e("Error with connection state: $t")
                }
            )
        operationDisposables.add(disposable)
    }

    override fun isBleEnabled(): Boolean {
        val state = rxBleClient.state
        return state != RxBleClient.State.BLUETOOTH_NOT_ENABLED
    }

    override fun stopScanning() {
        scanDisposable?.dispose()
        scanDisposable = null
    }
}