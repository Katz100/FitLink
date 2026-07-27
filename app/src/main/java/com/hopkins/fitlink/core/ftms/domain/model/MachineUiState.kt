package com.hopkins.fitlink.core.ftms.domain.model

sealed interface MachineUiState {
    data object DetectingMachine: MachineUiState

    data class TreadmillMachine(
        val instantaneousSpeed: Double?,
        val heartRate: Int?,
        val distance: Double = 0.0,
        val totalDistance: Int? = 0,
        val inclination: Double? = 0.0
    ): MachineUiState
}