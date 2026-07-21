package com.hopkins.fitlink.feature.workout

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import com.hopkins.fitlink.core.data.BleRepository
import com.hopkins.fitlink.core.data.ConnectionStatus
import com.hopkins.fitlink.core.data.NotificationChanged
import com.hopkins.fitlink.core.ftms.EquipmentType
import com.hopkins.fitlink.core.ftms.FTMSConstants
import com.hopkins.fitlink.core.ftms.Machine
import com.hopkins.fitlink.core.ftms.MachineState
import com.hopkins.fitlink.core.ftms.MachineState.TreadmillMachine
import com.hopkins.fitlink.core.ftms.Treadmill
import com.hopkins.fitlink.core.ftms.createMachine
import com.hopkins.fitlink.nav.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class WorkoutScreenViewModel @Inject constructor(
    private val bleRepository: BleRepository,
    savedStateHandle: SavedStateHandle,
): ViewModel() {

    private val deviceAddress = savedStateHandle.toRoute<Screen.ActiveWorkout>().macAddress
    private var machine: Machine<*>? = null

    private val _equipmentType = MutableStateFlow<EquipmentType>(EquipmentType.TREADMILL)
    val equipmentType = _equipmentType.asStateFlow()

    private val _notificationStatus = MutableStateFlow<NotificationChanged>(NotificationChanged.NotificationLoading)
    val notificationStatus = _notificationStatus.asStateFlow()

    private val _connectionState = MutableStateFlow<ConnectionStatus>(ConnectionStatus.ConnectionLoading)
    val connectionState = _connectionState.asStateFlow()

    private val _machineState = MutableStateFlow<MachineState>(MachineState.DetectingMachine)
    val machineState = _machineState.asStateFlow()

    init {
        connectToDevice()
    }

    private fun connectToDevice() {
        bleRepository.connectToDevice(
            deviceAddress = deviceAddress,
            connectionStatusChanged = {
                _connectionState.value = it
                if (it is ConnectionStatus.Connected) {
                    discoverCharacteristics()
                 //   bleRepository.writeToControlPoint()
                }
            }
        )
    }

    private fun discoverCharacteristics() {
        bleRepository.discoverCharacteristic(
            deviceAddress = deviceAddress,
            onEquipmentCharacteristicFound = { equipmentType ->
                _equipmentType.value = equipmentType
            },
            onFinished = {
                val characteristic = when(_equipmentType.value) {
                    EquipmentType.TREADMILL -> UUID.fromString(FTMSConstants.TREADMILL_CHARACTERISTIC)
                    EquipmentType.BIKE -> TODO()
                    EquipmentType.STAIR_MASTER -> TODO()
                }
                machine = createMachine(_equipmentType.value)
                subscribeToCharacteristic(deviceAddress, characteristic)
            }
        )
    }
    fun updateSpeed() {
        bleRepository.setSpeed(500.0, deviceAddress)
    }
    private fun subscribeToCharacteristic(
        deviceAddress: String,
        characteristicUUID: UUID
    ) {
        bleRepository.connectAndSubscribeToCharacteristic(
            deviceAddress = deviceAddress,
            characteristic = characteristicUUID,
            onBytesReceived = { bytes ->
                updateMachineState(bytes)
            },
            onNotificationChanged = { notification ->
                _notificationStatus.value = notification
            }
        )
    }

    private fun updateMachineState(bytes: ByteArray) {
        val currentMachine = machine ?: return
        currentMachine.parseDataForMachine(bytes)

        _machineState.value = when(currentMachine) {
            is Treadmill -> {
                TreadmillMachine(
                    instantaneousSpeed = currentMachine.machineData?.instantaneousSpeed,
                    heartRate = currentMachine.machineData?.heartRate,
                    inclination = currentMachine.machineData?.inclination
                )
            }
            else -> return
        }
    }
}

