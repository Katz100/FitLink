package com.hopkins.fitlink.core.ble

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat
import com.hopkins.fitlink.core.ble.FTMSConstants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import javax.inject.Inject

class FitBLE @Inject constructor(
    private val bluetoothAdapter: BluetoothAdapter?
) {
    companion object {
        const val TAG = "FitBLE"

        val BLE_PERMISSIONS = setOf(
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
        )

        fun isBLEPermissionsGranted(
            context: Context
        ): Boolean {
            return BLE_PERMISSIONS.all { permission ->
                ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
            }
        }
    }

    private val _devices = MutableStateFlow<Set<BluetoothDevice>>(emptySet())
    val devices: StateFlow<Set<BluetoothDevice>> = _devices.asStateFlow()

    private val _isScanning = MutableStateFlow<Boolean>(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    private val leScanCallback: ScanCallback = object : ScanCallback() {
        @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            val device = result.device
            val scanRecord = result.scanRecord

            if (!_devices.value.contains(device)) {
                val supportsFTMS = scanRecord?.serviceUuids?.any { service ->
                    service.uuid.toString() == FTMSConstants.FTMS_MACHINE
                } == true

                if (supportsFTMS) {
                    _devices.value = _devices.value + setOf(device)
                }
            }
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            Log.i("TAG", "Scanning failed: $errorCode")
        }
    }

    private val bluetoothLeScanner = bluetoothAdapter?.bluetoothLeScanner
    private val handler = Handler(Looper.getMainLooper())

    // Stops scanning after 10 seconds.
    private val SCAN_PERIOD: Long = 10000

    fun scanLeDevice(context: Context) {
        if (
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED
        ) return
        if (!_isScanning.value) { // Stops scanning after a pre-defined scan period.
            handler.postDelayed({
                _isScanning.value = false
                bluetoothLeScanner?.stopScan(leScanCallback)
            }, SCAN_PERIOD)
            _isScanning.value = true
            bluetoothLeScanner?.startScan(leScanCallback)
        } else {
            _isScanning.value = false
            bluetoothLeScanner?.stopScan(leScanCallback)
        }
    }

    fun isBLESupported(): Boolean {
        return bluetoothAdapter?.isEnabled == true
    }

    fun clearDevices() {
        _devices.value = emptySet()
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun enableBluetooth(context: Context) {
        if (isBLEPermissionsGranted(context) && bluetoothAdapter?.isEnabled == false)  {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            context.startActivity(enableBtIntent)
        }
    }
}