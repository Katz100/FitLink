package com.hopkins.fitlink.feature.workout

import com.hopkins.fitlink.core.domain.model.ConnectionStatus
import com.hopkins.fitlink.core.domain.model.NotificationChanged
import com.hopkins.fitlink.core.domain.model.EquipmentType
import com.hopkins.fitlink.feature.workout.MachineUiState
import com.polidea.rxandroidble3.RxBleConnection

data class WorkoutUiState(
    val equipmentType: EquipmentType = EquipmentType.TREADMILL,
    val rxConnectionState: RxBleConnection.RxBleConnectionState = RxBleConnection.RxBleConnectionState.DISCONNECTED,
    val notificationStatus: NotificationChanged = NotificationChanged.NotificationLoading,
    val connectionState: ConnectionStatus = ConnectionStatus.ConnectionLoading,
    val fitnessMachineStatus: FitnessMachineStatus = FitnessMachineStatus.Started,
    val machineUiState: MachineUiState = MachineUiState.DetectingMachine,
    val showAdditionalDetailsForTime: Boolean = false,
    val showAdditionalDetailsForCalories: Boolean = false,
    val showAdditionalDetailsForHearRate: Boolean = false,
)

interface FitnessMachineStatus {
    data object Reset : FitnessMachineStatus
    data object StoppedBySafetyKey : FitnessMachineStatus
    data object Started : FitnessMachineStatus
    data object Paused : FitnessMachineStatus
    data object Stopped : FitnessMachineStatus
    data object Unknown : FitnessMachineStatus
}