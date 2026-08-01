package com.hopkins.fitlink.core.ftms.domain.model

sealed interface MachineUiState {
    data object DetectingMachine: MachineUiState

    // TODO: Change types to String
    data class TreadmillMachine(
        val instantaneousSpeed: Double?,
        val heartRate: Int?,
        val distance: Double = 0.0,
        val totalDistance: Int? = 0,
        val inclination: Double? = 0.0,
        val elapsedTime: Int? = 0,
        val timeRemaining: Int? = 0,
        val calories: String,
        val caloriesAnHour: String,
        val watts: String,
    ): MachineUiState
}