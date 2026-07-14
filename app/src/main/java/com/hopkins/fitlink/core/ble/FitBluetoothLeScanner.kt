package com.hopkins.fitlink.core.ble

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import androidx.core.content.ContextCompat
import com.hopkins.fitlink.core.ble.FitBLE.Companion.isBLEPermissionsGranted
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

interface FitBluetoothLeScanner {
    val scanning: MutableStateFlow<Boolean>
    val bluetoothLeScanner: BluetoothLeScanner?
    val handler: Handler
    var bluetoothGatt: BluetoothGatt?

    fun connectToGATT(
        context: Context,
        device: BluetoothDevice,
        autoConnect: Boolean,
        bluetoothGattCallback: BluetoothGattCallback
    )

    fun scanLeDevice(
        context: Context,
        leScanCallback: ScanCallback,
    )

    fun stopScanning(
        leScanCallback: ScanCallback,
    )

    fun isBleSupported(): Boolean

    fun enableBluetooth(
        context: Context
    )
}

@SuppressLint("MissingPermission")
class FitBluetoothScannerImpl @Inject constructor(
    private val bluetoothAdapter: BluetoothAdapter?
) : FitBluetoothLeScanner {
    companion object {
        const val SCAN_PERIOD = 10_000L
    }
    override val scanning: MutableStateFlow<Boolean> = MutableStateFlow(false)
    override val bluetoothLeScanner: BluetoothLeScanner? = bluetoothAdapter?.bluetoothLeScanner
    override val handler: Handler = Handler(Looper.getMainLooper())
    override var bluetoothGatt: BluetoothGatt? = null

    override fun connectToGATT(
        context: Context,
        device: BluetoothDevice,
        autoConnect: Boolean,
        bluetoothGattCallback: BluetoothGattCallback
    ) {
        if (ContextCompat.checkSelfPermission(
                context, Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED) {
            bluetoothGatt = device.connectGatt(context, autoConnect, bluetoothGattCallback)
        }
    }

    override fun scanLeDevice(
        context: Context,
        leScanCallback: ScanCallback,
    ) {
        if (
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED
        ) return
        if (!scanning.value) {
            handler.postDelayed({
                scanning.value = false
                bluetoothLeScanner?.stopScan(leScanCallback)
            }, SCAN_PERIOD)
            scanning.value = true
            bluetoothLeScanner?.startScan(leScanCallback)
        } else {
            scanning.value = false
            bluetoothLeScanner?.stopScan(leScanCallback)
        }
    }

    override fun stopScanning(
        leScanCallback: ScanCallback,
    ) {
        bluetoothLeScanner?.stopScan(leScanCallback)
        scanning.value = false
    }

    override fun isBleSupported(): Boolean {
        return bluetoothAdapter?.isEnabled == true
    }

    override fun enableBluetooth(context: Context) {
        if (isBLEPermissionsGranted(context) && bluetoothAdapter?.isEnabled == false)  {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            context.startActivity(enableBtIntent)
        }
    }
}