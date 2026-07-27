package com.hopkins.fitlink.feature.workout

import com.hopkins.fitlink.core.data.ConnectionStatus
import com.hopkins.fitlink.core.data.NotificationChanged
import com.hopkins.fitlink.core.ftms.domain.model.EquipmentType
import com.hopkins.fitlink.core.ftms.domain.model.MachineUiState

data class WorkoutUiState(
    val equipmentType: EquipmentType = EquipmentType.TREADMILL,
    val notificationStatus: NotificationChanged = NotificationChanged.NotificationLoading,
    val connectionState: ConnectionStatus = ConnectionStatus.ConnectionLoading,
    val machineUiState: MachineUiState = MachineUiState.DetectingMachine
)