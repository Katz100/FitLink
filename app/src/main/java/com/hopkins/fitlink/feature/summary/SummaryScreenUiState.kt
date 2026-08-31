package com.hopkins.fitlink.feature.summary

data class SummaryUiState(
    val result: WorkoutResult = WorkoutResult.Loading
)

sealed interface WorkoutResult {
    data object Loading : WorkoutResult
    data class Success<T>(val data: T) : WorkoutResult
    data class Error(val message: String) : WorkoutResult
}