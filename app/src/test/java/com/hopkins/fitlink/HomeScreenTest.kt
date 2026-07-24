package com.hopkins.fitlink

import android.os.ParcelUuid
import com.hopkins.fitlink.core.data.BleRepository
import com.hopkins.fitlink.core.data.impl.BleRepositoryImpl
import com.hopkins.fitlink.core.ftms.FTMSConstants
import com.hopkins.fitlink.feature.home.HomeScreenViewModel
import com.polidea.rxandroidble3.RxBleClient
import com.polidea.rxandroidble3.mockrxandroidble.RxBleClientMock
import com.polidea.rxandroidble3.mockrxandroidble.RxBleConnectionMock
import com.polidea.rxandroidble3.mockrxandroidble.RxBleDeviceMock
import com.polidea.rxandroidble3.mockrxandroidble.RxBleScanRecordMock
import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.UUID
import kotlin.test.assertTrue

private const val deviceName = "TestDevice"
private const val macAddress = "AA:BB:CC:DD:EE:FF"
private const val rrsi = -42
private val serviceUuid = UUID.fromString(FTMSConstants.FTMS_MACHINE)
private val characteristicUuid = UUID.fromString(FTMSConstants.TREADMILL_CHARACTERISTIC)
private val treadmillData = byteArrayOf(
    0x00,
    0x00,
    0xF4.toByte(),
    0x01
)

@RunWith(RobolectricTestRunner::class)
class HomeScreenTest {
    private lateinit var mockClient: RxBleClient
    private lateinit var bleRepository: BleRepository
    private lateinit var viewModel: HomeScreenViewModel

    @Before
    fun setUp() {
        val connection = RxBleConnectionMock.Builder()
            .rssi(rrsi)
            .addService(
                serviceUuid,
                RxBleClientMock.CharacteristicsBuilder()
                    .addCharacteristic(
                        characteristicUuid,
                        treadmillData
                    )
                    .build()
            )
            .build()

        val device = RxBleDeviceMock.Builder()
            .deviceMacAddress(macAddress)
            .deviceName(deviceName)
            .scanRecord(
                RxBleScanRecordMock.Builder()
                    .setAdvertiseFlags(1)
                    .addServiceUuid(ParcelUuid(serviceUuid))
                    .setDeviceName(deviceName)
                    .build()
            )
            .connection(connection)
            .build()

        mockClient = RxBleClientMock.Builder()
            .addDevice(device)
            .build()

        bleRepository = BleRepositoryImpl(
            rxBleClient = mockClient
        )
        viewModel = HomeScreenViewModel(bleRepository)
    }

    @Test
    fun `Devices are empty on initialization`() {
        assertTrue(viewModel.devices.value.isEmpty())
    }

    @Test
    fun `Scanning for devices updates current list of device`() {
        viewModel.scanForDevices()

        assertTrue(viewModel.scanning.value == true)
        assertTrue(!viewModel.devices.value.isEmpty())
        assertEquals(deviceName, viewModel.devices.value[0].name)
    }
}