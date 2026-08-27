package com.hopkins.fitlink.core.data.room.entity

import androidx.room3.Entity
import androidx.room3.PrimaryKey
import com.hopkins.fitlink.core.data.room.RoomConstants

@Entity(tableName = RoomConstants.TREADMILL_TABLE)
data class TreadmillSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val avgSpeed: Double,
    val maxSpeed: Double,
    val maxIncline: Double,
    val avgPace: Double,
    val avgHr: Int,
    val maxHr: Int,
    val calories: Int,
    val maxWatts: Int,
    val duration: Int,
    val timestamp: Long = System.currentTimeMillis()
)

data class TreadmillSessionDomain(
    val id: Long? = null,

    var avgSpeed: Double = 0.0,
    var maxSpeed: Double = 0.0,
    var maxIncline: Double = 0.0,
    var avgPace: Double = 0.0,
    var avgHr: Int = 0,
    var maxHr: Int = 0,
    var calories: Int = 0,
    var maxWatts: Int = 0,
    var duration: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)

fun TreadmillSessionDomain.toEntity(): TreadmillSessionEntity =
    TreadmillSessionEntity(
        id = id ?: 0,
        avgSpeed = avgSpeed,
        maxSpeed = maxSpeed,
        maxIncline = maxIncline,
        avgPace = avgPace,
        avgHr = avgHr,
        maxHr = maxHr,
        calories = calories,
        maxWatts = maxWatts,
        duration = duration,
        timestamp = timestamp
    )

fun TreadmillSessionEntity.toDomain(): TreadmillSessionDomain =
    TreadmillSessionDomain(
        id = id,
        avgSpeed = avgSpeed,
        maxSpeed = maxSpeed,
        maxIncline = maxIncline,
        avgPace = avgPace,
        avgHr = avgHr,
        maxHr = maxHr,
        calories = calories,
        maxWatts = maxWatts,
        duration = duration,
        timestamp = timestamp
    )