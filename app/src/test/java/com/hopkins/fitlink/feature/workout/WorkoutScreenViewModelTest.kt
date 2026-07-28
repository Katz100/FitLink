package com.hopkins.fitlink.feature.workout

import android.os.ParcelUuid
import androidx.lifecycle.SavedStateHandle
import com.hopkins.fitlink.core.data.BleRepository
import com.hopkins.fitlink.core.data.ConnectionStatus
import com.hopkins.fitlink.core.data.impl.BleRepositoryImpl
import com.hopkins.fitlink.core.ftms.FTMSConstants
import com.hopkins.fitlink.core.ftms.domain.model.EquipmentType
import com.polidea.rxandroidble3.RxBleClient
import com.polidea.rxandroidble3.mockrxandroidble.RxBleClientMock
import com.polidea.rxandroidble3.mockrxandroidble.RxBleConnectionMock
import com.polidea.rxandroidble3.mockrxandroidble.RxBleDeviceMock
import com.polidea.rxandroidble3.mockrxandroidble.RxBleScanRecordMock
import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.kotlin.spy
import org.robolectric.RobolectricTestRunner
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals

private const val deviceName = "TestDevice"
private const val macAddress = "AA:BB:CC:DD:EE:FF"
private const val rssi = -42
private val serviceUuid = UUID.fromString(FTMSConstants.FTMS_MACHINE)
private val characteristicUuid = UUID.fromString(FTMSConstants.TREADMILL_CHARACTERISTIC)
private val treadmillData = byteArrayOf(
    0x00,
    0x00,
    0xF4.toByte(),
    0x01
)

@RunWith(RobolectricTestRunner::class)
class WorkoutScreenViewModelTest {
    private lateinit var mockClient: RxBleClient
    private lateinit var bleRepository: BleRepository
    private lateinit var viewModel: WorkoutScreenViewModel

    @Before
    fun setup() {
        val connection = RxBleConnectionMock.Builder()
            .rssi(rssi)
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

        val rxMockClient = RxBleClientMock.Builder()
            .addDevice(device)
            .build()

        mockClient = spy(rxMockClient)
        bleRepository = BleRepositoryImpl(
            rxBleClient = mockClient
        )
        val savedStateHandle = SavedStateHandle(
            mapOf("macAddress" to macAddress)
        )
        viewModel = WorkoutScreenViewModel(bleRepository, savedStateHandle)
    }

    @Test
    fun `Device is connected to ble device on initialization`() {
        assertEquals(ConnectionStatus.Connected, viewModel.workoutUiState.value.connectionState)
        assertEquals(EquipmentType.TREADMILL, viewModel.workoutUiState.value.equipmentType)
    }
}