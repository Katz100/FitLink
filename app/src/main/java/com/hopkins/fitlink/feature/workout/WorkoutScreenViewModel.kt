package com.hopkins.fitlink.feature.workout

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import com.hopkins.fitlink.core.data.BleRepository
import com.hopkins.fitlink.core.data.ConnectionStatus
import com.hopkins.fitlink.core.ftms.FTMSConstants
import com.hopkins.fitlink.core.ftms.domain.model.EquipmentType
import com.hopkins.fitlink.core.ftms.domain.model.Machine
import com.hopkins.fitlink.core.ftms.domain.model.MachineUiState
import com.hopkins.fitlink.core.ftms.domain.model.MachineUiState.DetectingMachine
import com.hopkins.fitlink.core.ftms.domain.model.MachineUiState.TreadmillMachine
import com.hopkins.fitlink.core.ftms.domain.model.Treadmill
import com.hopkins.fitlink.core.ftms.factory.createMachine
import com.hopkins.fitlink.nav.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class WorkoutScreenViewModel @Inject constructor(
    private val bleRepository: BleRepository,
    savedStateHandle: SavedStateHandle,
): ViewModel() {

    private val deviceAddress = savedStateHandle.toRoute<Screen.ActiveWorkout>().macAddress
    private var machine: Machine<*>? = null
    private var maxHeartRate: Int = 0

    private val _workoutUiState = MutableStateFlow<WorkoutUiState>(WorkoutUiState())
    val workoutUiState: StateFlow<WorkoutUiState> = _workoutUiState.asStateFlow()

    init {
        connectToDevice()
    }

    fun connectToDevice() {
        _workoutUiState.update {
            it.copy(
                connectionState = ConnectionStatus.ConnectionLoading,
                machineUiState = DetectingMachine
            )
        }

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
        bleRepository.subscribeToCharacteristic(
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
                        maxHeartRate = maxHeartRate.coerceAtLeast(
                            currentMachine.machineData?.heartRate?.toInt() ?: 0
                        )
                        TreadmillMachine(
                            instantaneousSpeed = currentMachine.machineData?.instantaneousSpeed,
                            heartRate = currentMachine.machineData?.heartRate?.takeIf { it > FTMSConstants.HEART_RATE_MIN && it < FTMSConstants.HEART_RATE_MAX } ?: 0,
                            inclination = currentMachine.machineData?.inclination,
                            elapsedTime = currentMachine.machineData?.elapsedTime,
                            timeRemaining = currentMachine.machineData?.remainingTime,
                            calories = currentMachine.machineData?.totalEnergy?.toString() ?: "--",
                            caloriesAnHour = currentMachine.machineData?.energyPerHour?.toString() ?: "--",
                            watts = currentMachine.machineData?.powerOutput?.toString() ?: "--",
                            maxHeartRate = maxHeartRate.toString()
                        )
                    }
                    else -> return
                }
            )
        }
    }
}

