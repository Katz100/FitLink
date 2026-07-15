package com.hopkins.fitlink.core.data.impl

import android.os.ParcelUuid
import com.hopkins.fitlink.core.data.BleRepository
import com.hopkins.fitlink.core.data.EQUIPMENT_TYPE
import com.hopkins.fitlink.core.ftms.FTMSConstants
import com.polidea.rxandroidble3.RxBleClient
import com.polidea.rxandroidble3.RxBleDevice
import com.polidea.rxandroidble3.scan.ScanFilter
import com.polidea.rxandroidble3.scan.ScanSettings
import io.reactivex.rxjava3.disposables.Disposable
import timber.log.Timber
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class BleRepositoryImpl @Inject constructor(
    private val rxBleClient: RxBleClient
): BleRepository {
    companion object {
        const val TAG = "BleRepository"
        const val TIMEOUT = 10L
    }

    private var scanDisposable: Disposable? = null
    private var connectDisposable: Disposable? = null

    override fun scanDevices(
        onDeviceScanned: (RxBleDevice) -> Unit,
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
                    onDeviceScanned(scanResult.bleDevice)
                },
                { throwable ->
                    Timber.tag(TAG).e(throwable, "There was an error scanning devices")
                }
            )
    }

    override fun connectAndSubscribeToCharacteristic(
        characteristic: UUID,
        device: RxBleDevice,
        onBytesReceived: (ByteArray) -> Unit,
        onNotificationCreated: () -> Unit,
        onNotificationEnded: () -> Unit,
        onNotificationError: (Throwable) -> Unit,
    ) {
        stopScanning()
        connectDisposable?.dispose()

        connectDisposable = device.establishConnection(false)
            .flatMap { connection ->
                connection.setupNotification(characteristic)
            }
            .doOnNext {
                Timber.tag(TAG).i("Notification set up")
                onNotificationCreated()
            }
            .flatMap { stream ->
                stream
            }
            .doFinally {
                connectDisposable = null
                Timber.tag(TAG).i("Connection / notification stream ended")
                onNotificationEnded()
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
                    onNotificationError(e)
                }
            )
    }

    override fun connectToDevice(device: RxBleDevice) {
        TODO("Not yet implemented")
    }

    override fun discoverCharacteristic(
        device: RxBleDevice,
        onEquipmentCharacteristicFound: (EQUIPMENT_TYPE) -> Unit,
        onFinished: () -> Unit,
    ) {
        stopScanning()
        connectDisposable?.dispose()

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
                 onFinished()
             }
            .subscribe(
                { characteristic ->
                    characteristic.forEach { ch ->
                        if (ch.uuid == UUID.fromString(FTMSConstants.TREADMILL_CHARACTERISTIC)) {
                            Timber.tag(TAG).i("Found Treadmill Characteristic")
                            onEquipmentCharacteristicFound(EQUIPMENT_TYPE.TREADMILL)
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

    private fun stopScanning() {
        scanDisposable?.dispose()
        scanDisposable = null
    }
}
