package com.hopkins.fitlink.core.domain.model

data class TreadmillData(
    val moreData: Boolean,
    val instantaneousSpeed: Double?,
    val averageSpeed: Double?,
    val totalDistance: Int?,
    val inclination: Double?,
    val rampAngleSetting: Double?,
    val positiveElevationGain: Int?,
    val negativeElevationGain: Int?,
    val instantaneousPace: Double?,
    val averagePace: Double?,
    val totalEnergy: Double?,
    val energyPerHour: Int?,
    val energyPerMinute: Int?,
    val heartRate: Int?,
    val metabolicEquivalent: Double?,
    val elapsedTime: Int?,
    val remainingTime: Int?,
    val forceOnBelt: Int?,
    val powerOutput: Int?
)