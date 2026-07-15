package com.hopkins.fitlink.core.data.impl

import com.hopkins.fitlink.core.data.BleRepository
import com.polidea.rxandroidble3.RxBleClient
import com.polidea.rxandroidble3.RxBleDevice
import com.polidea.rxandroidble3.scan.ScanFilter
import com.polidea.rxandroidble3.scan.ScanSettings
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class BleRepositoryImpl @Inject constructor(
    private val rxBleClient: RxBleClient
): BleRepository {
    companion object {
        const val TAG = "BleRepository"
    }

    override fun scanDevices(
        onDeviceScanned: (RxBleDevice) -> Unit,
        onScanningFinished: () -> Unit,
    ) {
        val scanSettings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
            .build()

        val scanFilter = ScanFilter.Builder()
            .build()


        val scanSubscription = rxBleClient
            .scanBleDevices(scanSettings, scanFilter)
            .take(10, TimeUnit.SECONDS)
            .subscribe (
                { device ->
                    onDeviceScanned(device.bleDevice)
                },
                { throwable ->
                    Timber.tag(TAG).e("There was an error scanning devices: ${throwable.message}")
                },
                {
                    Timber.tag(TAG).i("Scanning has finished")
                    onScanningFinished()
                }
            )
    }
}
