package com.hopkins.fitlink.core.ftms

import com.hopkins.fitlink.Workout.hasFlag
import com.polidea.rxandroidble3.helpers.ValueInterpreter

abstract class Machine<Data>(
) {
    abstract var machineData: Data?

    abstract fun parseDataForMachine(bytes: ByteArray)
}

class Treadmill: Machine<TreadmillData>(
) {
    override var machineData: TreadmillData? = TreadmillData(
        moreData = false,
        instantaneousSpeed = 0.0,
        averageSpeed = 0.0,
        totalDistance = 0,
        inclination = 0.0,
        rampAngleSetting = 0.0,
        positiveElevationGain = 0,
        negativeElevationGain = 0,
        instantaneousPace = 0.0,
        averagePace = 0.0,
        totalEnergy = 0,
        energyPerHour = 0,
        energyPerMinute = 0,
        heartRate = 0,
        metabolicEquivalent = 0.0,
        elapsedTime = 0,
        remainingTime = 0,
        forceOnBelt = 0,
        powerOutput = 0
    )

    override fun parseDataForMachine(bytes: ByteArray) {
        var offset = 0
        val flags = ValueInterpreter.getIntValue(
             bytes,
             ValueInterpreter.FORMAT_UINT16,
             offset
         )
        offset +=2

        if (hasFlag(0, flags)) return

        val instantaneousSpeed = ValueInterpreter.getIntValue(
            bytes,
            ValueInterpreter.FORMAT_UINT16,
            offset
        ) / 100.0

        val speedMph = instantaneousSpeed * FTMSConstants.MPH_CONSTANT

        offset += 2

        val heartRate = ValueInterpreter.getIntValue(
            bytes,
            ValueInterpreter.FORMAT_UINT8,
            offset
        )

        machineData = machineData?.copy(
            instantaneousSpeed = speedMph,
            heartRate = heartRate
        )
    }
}

/*
                val flags = ValueInterpreter.getIntValue(
                    bytes,
                    ValueInterpreter.FORMAT_UINT16,
                    0,
                )?: return@connectAndSubscribeToCharacteristic

                if (hasFlag(0, flags)) return@connectAndSubscribeToCharacteristic

                val instant = ValueInterpreter.getIntValue(
                    bytes,
                    ValueInterpreter.FORMAT_UINT16,
                    2
                )?: return@connectAndSubscribeToCharacteristic

                val speedKph = instant / 100.0
                val speedMph = speedKph * 0.621371

                _speed.value = speedMph
 */

enum class EquipmentType {
    TREADMILL,
    BIKE,
    STAIR_MASTER,
}

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
    val totalEnergy: Int?,
    val energyPerHour: Int?,
    val energyPerMinute: Int?,
    val heartRate: Int?,
    val metabolicEquivalent: Double?,
    val elapsedTime: Int?,
    val remainingTime: Int?,
    val forceOnBelt: Int?,
    val powerOutput: Int?
)