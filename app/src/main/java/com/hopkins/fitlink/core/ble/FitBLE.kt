package com.hopkins.fitlink.core.ble

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat
import com.hopkins.fitlink.core.ftms.FTMSConstants
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject

@SuppressLint("MissingPermission")
class FitBLE @Inject constructor(
    private val fitBluetoothLeScanner: FitBluetoothLeScanner,
    @ApplicationContext context: Context,
) {
    companion object {
        private const val TAG = "FitBLE"

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

    val isScanning: StateFlow<Boolean> = fitBluetoothLeScanner.scanning.asStateFlow()

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
                        if (gatt == fitBluetoothLeScanner.bluetoothGatt) {
                            fitBluetoothLeScanner.bluetoothGatt = null
                        }
                    }
                }
            } else {
                Timber.tag(TAG).w("GATT connection failed: status=$status newState=$newState")
                _connectivity.value = Connectivity.CONNECTION_ERROR
                gatt?.disconnect()
                gatt?.close()
                if (gatt == fitBluetoothLeScanner.bluetoothGatt) {
                    fitBluetoothLeScanner.bluetoothGatt = null
                }
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
                setCharacteristicNotification(treadmillCharacteristic, true)
            }
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray,
            status: Int
        ) {
            super.onCharacteristicRead(gatt, characteristic, value, status)
            if (status == BluetoothGatt.GATT_SUCCESS) {
                val hexString: String = value.joinToString(separator = " ") {
                    String.format("%02X", it)
                }
                Timber.tag(TAG).i("Read characteristic containing: $hexString")
            }
        }

        override fun onDescriptorRead(
            gatt: BluetoothGatt,
            descriptor: BluetoothGattDescriptor,
            status: Int,
            value: ByteArray
        ) {
            Timber.tag(TAG).d(
                "Descriptor read: ${descriptor.uuid}, status=$status, value=${value.joinToString()}"
            )
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray
        ) {
            super.onCharacteristicChanged(gatt, characteristic, value)
//            val speed = FTMSConstants.parseSpeed(value)
//            Timber.tag(TAG).d("Speed: $speed")
//            if (speed != 0.0) {
//                Toast.makeText(context, "Speed: $speed", Toast.LENGTH_SHORT).show()
//            }
        }
    }

    fun setCharacteristicNotification(
        characteristic: BluetoothGattCharacteristic,
        enabled: Boolean
    ) {
        fitBluetoothLeScanner.bluetoothGatt?.let { gatt ->
            gatt.setCharacteristicNotification(characteristic, enabled)
            val ccd = characteristic.getDescriptor(UUID.fromString(FTMSConstants.CLIENT_CHARACTERISTIC_CONFIG))
            ccd.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
            gatt.writeDescriptor(ccd)
        } ?: run {
            Timber.tag(TAG).e("BluetoothGatt not initialized")

        }
    }

    fun readCharacteristic(characteristic: BluetoothGattCharacteristic) {
        fitBluetoothLeScanner.bluetoothGatt?.readCharacteristic(characteristic) ?: run {
            Log.w(TAG, "BluetoothGatt not initialized")
            return
        }
    }


    fun connectToGATT(
        context: Context,
        device: BluetoothDevice,
        autoConnect: Boolean,
    ) {
        fitBluetoothLeScanner.stopScanning(leScanCallback)
        _connectivity.value = Connectivity.CONNECTING
        fitBluetoothLeScanner.connectToGATT(context, device, autoConnect, bluetoothGattCallback)
    }

    fun scanLeDevice(context: Context) {
        fitBluetoothLeScanner.scanLeDevice(context, leScanCallback)
    }

    fun stopScanning() {
        fitBluetoothLeScanner.stopScanning(leScanCallback)
    }

    fun isBLESupported(): Boolean {
        return fitBluetoothLeScanner.isBleSupported()
    }

    fun clearDevices() {
        _devices.value = emptySet()
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun enableBluetooth(context: Context) {
        fitBluetoothLeScanner.enableBluetooth(context)
    }
}

enum class Connectivity {
    DISCONNECTED,
    CONNECTED,
    CONNECTING,
    CONNECTION_ERROR,
}
