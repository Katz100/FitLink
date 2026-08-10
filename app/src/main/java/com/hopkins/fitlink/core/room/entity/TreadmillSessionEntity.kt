package com.hopkins.fitlink.core.room.entity

import androidx.room3.Entity
import androidx.room3.PrimaryKey
import com.hopkins.fitlink.core.room.RoomConstants

@Entity(tableName = RoomConstants.TREADMILL_TABLE)
data class TreadmillMetricsEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val avgSpeed: Double,
    val maxSpeed: Double,
    val avgIncline: Double,
    val maxIncline: Double,
    val avgPace: Double,
    val minHr: Int,
    val avgHr: Int,
    val maxHr: Int,
    val calories: Int,
    val avgWatts: Int,
    val maxWatts: Int,
    val duration: Int,
    val timestamp: Long = System.currentTimeMillis()
)

data class TreadmillSessionDomain(
    val id: Long = 0,

    val avgSpeed: Double,
    val maxSpeed: Double,
    val avgIncline: Double,
    val maxIncline: Double,
    val avgPace: Double,
    val minHr: Int,
    val avgHr: Int,
    val maxHr: Int,
    val calories: Int,
    val avgWatts: Int,
    val maxWatts: Int,
    val duration: Int,
    val timestamp: Long = System.currentTimeMillis()
)

fun TreadmillSessionDomain.toEntity(): TreadmillMetricsEntity =
    TreadmillMetricsEntity(
        id = id,
        avgSpeed = avgSpeed,
        maxSpeed = maxSpeed,
        avgIncline = avgIncline,
        maxIncline = maxIncline,
        avgPace = avgPace,
        minHr = minHr,
        avgHr = avgHr,
        maxHr = maxHr,
        calories = calories,
        avgWatts = avgWatts,
        maxWatts = maxWatts,
        duration = duration,
        timestamp = timestamp
    )

fun TreadmillMetricsEntity.toDomain(): TreadmillSessionDomain =
    TreadmillSessionDomain(
        id = id,
        avgSpeed = avgSpeed,
        maxSpeed = maxSpeed,
        avgIncline = avgIncline,
        maxIncline = maxIncline,
        avgPace = avgPace,
        minHr = minHr,
        avgHr = avgHr,
        maxHr = maxHr,
        calories = calories,
        avgWatts = avgWatts,
        maxWatts = maxWatts,
        duration = duration,
        timestamp = timestamp
    )