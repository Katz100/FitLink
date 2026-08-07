package com.hopkins.fitlink.core.room.entity

import androidx.room3.Entity
import androidx.room3.PrimaryKey
import com.hopkins.fitlink.core.ftms.domain.model.MachineUiState

@Entity(tableName = "treadmill_metrics")
data class TreadmillMetricsEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

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
    val maxHeartRate: String,
    val timestamp: Long = System.currentTimeMillis()
)

fun MachineUiState.TreadmillMachine.toEntity(): TreadmillMetricsEntity {
    return TreadmillMetricsEntity(
        instantaneousSpeed = instantaneousSpeed,
        heartRate = heartRate,
        distance = distance,
        totalDistance = totalDistance,
        inclination = inclination,
        elapsedTime = elapsedTime,
        timeRemaining = timeRemaining,
        calories = calories,
        caloriesAnHour = caloriesAnHour,
        watts = watts,
        maxHeartRate = maxHeartRate
    )
}