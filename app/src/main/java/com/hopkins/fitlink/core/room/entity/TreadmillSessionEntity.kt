package com.hopkins.fitlink.core.room.entity

import androidx.room3.Entity
import androidx.room3.PrimaryKey
import com.hopkins.fitlink.core.room.RoomConstants

@Entity(tableName = RoomConstants.TREADMILL_TABLE)
data class TreadmillSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val avgSpeed: Double,
    val maxSpeed: Double,
    val avgIncline: Double,
    val maxIncline: Double,
    val avgPace: Double,
    val avgHr: Int,
    val maxHr: Int,
    val calories: Int,
    val avgWatts: Int,
    val maxWatts: Int,
    val duration: Int,
    val timestamp: Long = System.currentTimeMillis()
)

data class TreadmillSessionDomain(
    val id: Long? = null,

    var avgSpeed: Double = 0.0,
    var maxSpeed: Double = 0.0,
    val avgIncline: Double = 0.0,
    var maxIncline: Double = 0.0,
    val avgPace: Double = 0.0,
    val avgHr: Int = 0,
    var maxHr: Int = 0,
    val calories: Int = 0,
    val avgWatts: Int = 0,
    var maxWatts: Int = 0,
    val duration: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)

fun TreadmillSessionDomain.toEntity(): TreadmillSessionEntity =
    TreadmillSessionEntity(
        id = id ?: 0,
        avgSpeed = avgSpeed,
        maxSpeed = maxSpeed,
        avgIncline = avgIncline,
        maxIncline = maxIncline,
        avgPace = avgPace,
        avgHr = avgHr,
        maxHr = maxHr,
        calories = calories,
        avgWatts = avgWatts,
        maxWatts = maxWatts,
        duration = duration,
        timestamp = timestamp
    )

fun TreadmillSessionEntity.toDomain(): TreadmillSessionDomain =
    TreadmillSessionDomain(
        id = id,
        avgSpeed = avgSpeed,
        maxSpeed = maxSpeed,
        avgIncline = avgIncline,
        maxIncline = maxIncline,
        avgPace = avgPace,
        avgHr = avgHr,
        maxHr = maxHr,
        calories = calories,
        avgWatts = avgWatts,
        maxWatts = maxWatts,
        duration = duration,
        timestamp = timestamp
    )