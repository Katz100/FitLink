package com.hopkins.fitlink.core.ble

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothProfile
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject

class FitBLE @Inject constructor(
    private val bluetoothAdapter: BluetoothAdapter?
) {
    companion object {
        private const val TAG = "FitBLE"
        private const val SCAN_PERIOD = 10_000L

        val BLE_PERMISSIONS = arrayOf(
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

    private val _connectivity = MutableStateFlow<Connectivity>(Connectivity.DISCONNECTED)
    val connectivity: StateFlow<Connectivity> = _connectivity.asStateFlow()

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

                if (supportsFTMS && device != null) {
                    Timber.Forest.tag(TAG).d("""
                    Name: ${device.name ?: (scanRecord.deviceName)}
                    Address: ${device.address}
                    Service UUIDs: ${scanRecord.serviceUuids}
                    Service Data: ${scanRecord.serviceData}
                """.trimIndent())
                    _devices.value = _devices.value + setOf(device)
                }
            }
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            Timber.tag(TAG).e("Scanning failed: $errorCode")
        }
    }

    private val bluetoothGattCallback = object : BluetoothGattCallback() {
        @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                when (newState) {
                    BluetoothProfile.STATE_CONNECTED -> {
                        Timber.tag(TAG).i("Connected; discovering services")
                        _connectivity.value = Connectivity.CONNECTED
                        gatt?.discoverServices()
                    }

                    BluetoothProfile.STATE_DISCONNECTED -> {
                        Timber.tag(TAG).i("Disconnected")
                        _connectivity.value = Connectivity.DISCONNECTED
                        gatt?.close()
                        if (gatt == bluetoothGatt) bluetoothGatt = null
                    }
                }
            } else {
                Timber.tag(TAG).w("GATT connection failed: status=$status newState=$newState")
                _connectivity.value = Connectivity.CONNECTION_ERROR
                gatt?.disconnect()
                gatt?.close()
                if (gatt == bluetoothGatt) bluetoothGatt = null
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            super.onServicesDiscovered(gatt, status)
            if (status != BluetoothGatt.GATT_SUCCESS) {
                Timber.tag(TAG).i("Service discovery failed: $status")
                return
            }

            Log.d(TAG, "Services discovered")

            gatt?.services?.forEach { service ->
                Timber.tag(TAG).d("Service: ${service.uuid}")


                service.characteristics.forEach { characteristic ->
                    Timber.tag(TAG).d(
                        "Characteristic: ${characteristic.uuid}, properties=${characteristic.properties}"
                    )
                    characteristic.descriptors.forEach { descriptor ->
                        Timber.tag(TAG).d("Descriptor: ${descriptor.uuid}")
                    }
                    Timber.tag(TAG).i("End descriptors for characteristic: ${characteristic.uuid}")
                }
                Timber.tag(TAG).i("End Characteristics for service: ${service.uuid}")
            }

            val ftmsService = gatt?.getService(UUID.fromString(FTMSConstants.FTMS_MACHINE))

            if (ftmsService == null) {
                Timber.tag(TAG).i("FTMS service not found")
                return
            }

            val treadmillCharacteristic = ftmsService.getCharacteristic(
                UUID.fromString(FTMSConstants.TREADMILL_CHARACTERISTIC)
            )

            if (treadmillCharacteristic != null) {
                Timber.tag(TAG).i("Machine is a treadmill")
            }
        }

        override fun onDescriptorRead(
            gatt: BluetoothGatt,
            descriptor: android.bluetooth.BluetoothGattDescriptor,
            status: Int,
            value: ByteArray
        ) {
            Timber.tag(TAG).d(
                "Descriptor read: ${descriptor.uuid}, status=$status, value=${value.joinToString()}"
            )
        }
    }

    private val bluetoothLeScanner = bluetoothAdapter?.bluetoothLeScanner
    private val handler = Handler(Looper.getMainLooper())
    private var bluetoothGatt: BluetoothGatt? = null

    fun connectToGATT(
        context: Context,
        device: BluetoothDevice,
        autoConnect: Boolean,
    ) {
        if (ContextCompat.checkSelfPermission(
                context, Manifest.permission.BLUETOOTH_CONNECT
        ) == PackageManager.PERMISSION_GRANTED) {
            _connectivity.value = Connectivity.CONNECTING
            bluetoothGatt = device.connectGatt(context, autoConnect, bluetoothGattCallback)
        }
    }

    // Stops scanning after 10 seconds.
    fun scanLeDevice(context: Context) {
        if (
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED
        ) return
        if (!_isScanning.value) {
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

enum class Connectivity {
    DISCONNECTED,
    CONNECTED,
    CONNECTING,
    CONNECTION_ERROR,
}
