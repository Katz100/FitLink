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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID
import javax.inject.Inject

data class WorkoutUiState(
    val equipmentType: EquipmentType = EquipmentType.TREADMILL,
    val notificationStatus: NotificationChanged = NotificationChanged.NotificationLoading,
    val connectionState: ConnectionStatus = ConnectionStatus.ConnectionLoading,
    val machineUiState: MachineState = MachineState.DetectingMachine
)

@HiltViewModel
class WorkoutScreenViewModel @Inject constructor(
    private val bleRepository: BleRepository,
    savedStateHandle: SavedStateHandle,
): ViewModel() {

    private val deviceAddress = savedStateHandle.toRoute<Screen.ActiveWorkout>().macAddress
    private var machine: Machine<*>? = null

    private val _workoutUiState = MutableStateFlow<WorkoutUiState>(WorkoutUiState())
    val workoutUiState: StateFlow<WorkoutUiState> = _workoutUiState.asStateFlow()

    init {
        connectToDevice()
    }

    private fun connectToDevice() {
        bleRepository.connectToDevice(
            deviceAddress = deviceAddress,
            connectionStatusChanged = { connectionStatus ->
                _workoutUiState.update {
                    it.copy(
                        connectionState = connectionStatus
                    )
                }
                if (connectionStatus is ConnectionStatus.Connected) {
                    discoverCharacteristics()
                    bleRepository.writeToControlPoint()
                }
            }
        )
    }

    private fun discoverCharacteristics() {
        bleRepository.discoverCharacteristic(
            deviceAddress = deviceAddress,
            onEquipmentCharacteristicFound = { equipmentType ->
                _workoutUiState.update {
                    it.copy(
                        equipmentType = equipmentType
                    )
                }
            },
            onFinished = {
                val characteristic = when(_workoutUiState.value.equipmentType) {
                    EquipmentType.TREADMILL -> UUID.fromString(FTMSConstants.TREADMILL_CHARACTERISTIC)
                    EquipmentType.BIKE -> TODO()
                    EquipmentType.STAIR_MASTER -> TODO()
                }
                machine = createMachine(_workoutUiState.value.equipmentType)
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
                _workoutUiState.update {
                    it.copy(
                        notificationStatus = notification
                    )
                }
            }
        )
    }

    private fun updateMachineState(bytes: ByteArray) {
        val currentMachine = machine ?: return
        currentMachine.parseDataForMachine(bytes)

        _workoutUiState.update {
            it.copy(
                machineUiState = when(currentMachine) {
                    is Treadmill -> {
                        TreadmillMachine(
                            instantaneousSpeed = currentMachine.machineData?.instantaneousSpeed,
                            heartRate = currentMachine.machineData?.heartRate,
                            inclination = currentMachine.machineData?.inclination
                        )
                    }
                    else -> return
                }
            )
        }
    }
}

