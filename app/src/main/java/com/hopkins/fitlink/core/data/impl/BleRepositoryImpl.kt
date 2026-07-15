package com.hopkins.fitlink.core.data.impl

import com.hopkins.fitlink.core.data.BleRepository
import com.polidea.rxandroidble3.RxBleClient
import com.polidea.rxandroidble3.RxBleDevice
import com.polidea.rxandroidble3.scan.ScanFilter
import com.polidea.rxandroidble3.scan.ScanSettings
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class BleRepositoryImpl @Inject constructor(
    private val rxBleClient: RxBleClient
): BleRepository {
    companion object {
        const val TAG = "BleRepository"
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

        val scanFilter = ScanFilter.Builder()
            .build()


        scanDisposable = rxBleClient
            .scanBleDevices(scanSettings, scanFilter)
            .take(10, TimeUnit.SECONDS)
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

    override fun connectToDevice(device: RxBleDevice) {
        stopScanning()

        connectDisposable?.dispose()

        connectDisposable = device.establishConnection(false)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .doFinally {
                connectDisposable = null
                Timber.tag(TAG).i("Connection observable has been disposed")
            }
            .subscribe(
                { connection ->
                    Timber.tag(TAG).i("Connected to ${device.name ?: device.macAddress}")
                },
                { throwable ->
                    Timber.tag(TAG).e(throwable, "There was an error connecting to device: ${device.macAddress}")
                }
            )
    }

    private fun stopScanning() {
        scanDisposable?.dispose()
        scanDisposable = null
    }
}
