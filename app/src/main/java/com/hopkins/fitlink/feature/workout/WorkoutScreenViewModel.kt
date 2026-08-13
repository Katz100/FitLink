package com.hopkins.fitlink.feature.workout

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import com.hopkins.fitlink.core.data.BleRepository
import com.hopkins.fitlink.core.data.ConnectionStatus
import com.hopkins.fitlink.core.ftms.FTMSConstants
import com.hopkins.fitlink.core.ftms.domain.model.EquipmentType
import com.hopkins.fitlink.core.ftms.domain.model.Machine
import com.hopkins.fitlink.core.ftms.domain.model.MachineUiState.DetectingMachine
import com.hopkins.fitlink.core.ftms.domain.model.MachineUiState.TreadmillMachine
import com.hopkins.fitlink.core.ftms.domain.model.Treadmill
import com.hopkins.fitlink.core.ftms.factory.createMachine
import com.hopkins.fitlink.core.ftms.util.parseFitnessMachineStatus
import com.hopkins.fitlink.core.room.entity.TreadmillSessionDomain
import com.hopkins.fitlink.core.room.entity.toEntity
import com.hopkins.fitlink.nav.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class WorkoutScreenViewModel @Inject constructor(
    private val bleRepository: BleRepository,

    savedStateHandle: SavedStateHandle,
): ViewModel() {

    companion object {
        private const val TAG = "WorkoutScreenViewModel"
    }
    private val deviceAddress = savedStateHandle.toRoute<Screen.ActiveWorkout>().macAddress
    private var machine: Machine<*>? = null
    private val treadmillSession = TreadmillSessionDomain()

    private val _workoutUiState = MutableStateFlow<WorkoutUiState>(WorkoutUiState())
    val workoutUiState: StateFlow<WorkoutUiState> = _workoutUiState.asStateFlow()

    init {
        connectToDevice()

        bleRepository.subscribeToConnectionState(
            deviceAddress = deviceAddress,
        ) { connectionState ->
            _workoutUiState.update {
                it.copy(
                    rxConnectionState = connectionState
                )
            }
        }
    }

    fun disconnectFromDevice() {
        bleRepository.disconnectFromDevice()
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
                    subscribeToMachineStatusCharacteristic()
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
                _workoutUiState.update {
                    it.copy(
                        machineUiState = when(machine) {
                            is Treadmill -> {
                                TreadmillMachine(
                                    instantaneousSpeed = null,
                                    heartRate = null,
                                    inclination = null,
                                    elapsedTime = null,
                                    timeRemaining = null,
                                    calories = "--",
                                    caloriesAnHour = "--",
                                    watts = "--",
                                    maxHeartRate = "--"
                                )
                            }
                            else -> TODO()
                        }
                    )
                }
                subscribeToMachineCharacteristic(deviceAddress, characteristic)
            }
        )
    }

    private fun subscribeToMachineStatusCharacteristic() {
        val statusCharacteristic = UUID.fromString(FTMSConstants.FITNESS_MACHINE_STATUS)
        bleRepository.subscribeToCharacteristic(
            characteristic = statusCharacteristic,
            deviceAddress = deviceAddress,
            onBytesReceived = { bytes ->
                _workoutUiState.update {
                    it.copy(
                        fitnessMachineStatus = parseFitnessMachineStatus(bytes)
                    )
                }
                Timber.tag(TAG).i("Received new status: ${_workoutUiState.value.fitnessMachineStatus}")
            },
            onNotificationChanged = {
                Timber.tag(TAG).i("Status changed: $it")
            }
        )
    }
    private fun subscribeToMachineCharacteristic(
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
        updateWorkoutSessionInfo()

        _workoutUiState.update {
            it.copy(
                machineUiState = when(currentMachine) {
                    is Treadmill -> {
                        TreadmillMachine(
                            instantaneousSpeed = currentMachine.machineData?.instantaneousSpeed,
                            heartRate = currentMachine.machineData?.heartRate?.takeIf { it > FTMSConstants.HEART_RATE_MIN && it < FTMSConstants.HEART_RATE_MAX } ?: 0,
                            inclination = currentMachine.machineData?.inclination,
                            elapsedTime = currentMachine.machineData?.elapsedTime,
                            timeRemaining = currentMachine.machineData?.remainingTime,
                            calories = currentMachine.machineData?.totalEnergy?.toString() ?: "--",
                            caloriesAnHour = currentMachine.machineData?.energyPerHour?.toString() ?: "--",
                            watts = currentMachine.machineData?.powerOutput?.toString() ?: "--",
                            maxHeartRate = treadmillSession.maxHr.toString()
                        )
                    }
                    else -> return
                }
            )
        }
    }

    private fun updateWorkoutSessionInfo() {
        val currentMachine = machine ?: return
        when (currentMachine) {
            is Treadmill -> {
                treadmillSession.maxHr = treadmillSession.maxHr.coerceAtLeast(
                    currentMachine.machineData?.heartRate?.toInt()
                        ?.takeIf {
                            it > FTMSConstants.HEART_RATE_MIN && it < FTMSConstants.HEART_RATE_MAX
                        } ?: 0
                )

                treadmillSession.maxSpeed = treadmillSession.maxSpeed.coerceAtLeast(
                    (currentMachine.machineData?.instantaneousSpeed ?: 0.0)
                )

                treadmillSession.maxWatts = treadmillSession.maxWatts.coerceAtLeast(
                    (currentMachine.machineData?.powerOutput ?: 0)
                )

                treadmillSession.maxIncline = treadmillSession.maxIncline.coerceAtLeast(
                    (currentMachine.machineData?.inclination ?: 0.0)
                )


            }
        }
    }
}

