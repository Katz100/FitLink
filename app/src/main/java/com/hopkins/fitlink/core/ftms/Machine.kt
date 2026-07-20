package com.hopkins.fitlink.core.ftms

import com.hopkins.fitlink.feature.workout.hasFlag
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
        totalEnergy = 0.0,
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
        ) ?: return

        offset += 2

        var speedMph: Double? = null
        var averageSpeed: Double? = null
        var totalDistance: Int? = null
        var inclinationAngle: Double? = null
        var inclinationPercent: Double? = null
        var positiveGain: Int? = null
        var negativeGain: Int? = null
        var instantPace: Double? = null
        var averagePace: Double? = null
        var totalEnergyKcal: Double? = null
        var energyPerHourKcal: Double? = null
        var energyPerMinuteKcal: Double? = null
        var heartRateBpm: Double? = null
        var metabolicEquivalent: Double? = null
        var elapsedTimeSeconds: Double? = null
        var remainingTimeSeconds: Double? = null
        var forceOnBeltNewtons: Double? = null
        var powerOutputWatts: Double? = null

        if (!hasFlag(0, flags)) {
            val rawSpeed = ValueInterpreter.getIntValue(
                bytes,
                ValueInterpreter.FORMAT_UINT16,
                offset
            ) ?: return

            speedMph =
                rawSpeed * 0.01 * FTMSConstants.MPH_CONSTANT

            offset += 2
        }

        if (hasFlag(1, flags)) {
            val rawAverageSpeed = ValueInterpreter.getIntValue(
                bytes,
                ValueInterpreter.FORMAT_UINT16,
                offset
            ) ?: return

            averageSpeed =
                rawAverageSpeed * 0.01 * FTMSConstants.MPH_CONSTANT

            offset += 2
        }

        if (hasFlag(2, flags)) {
            totalDistance = getUInt24(bytes, offset) ?: return
            offset += 3
        }

        if (hasFlag(3, flags)) {
            val rawInclination = ValueInterpreter.getIntValue(
                bytes,
                ValueInterpreter.FORMAT_SINT16,
                offset
            ) ?: return

            inclinationPercent = rawInclination * 0.1
            offset += 2

            val rawRampAngle = ValueInterpreter.getIntValue(
                bytes,
                ValueInterpreter.FORMAT_SINT16,
                offset
            ) ?: return

            inclinationAngle =
                if (rawRampAngle == Short.MAX_VALUE.toInt()) {
                    0.0
                } else {
                    rawRampAngle * 0.1
                }

            offset += 2
        }

        if (hasFlag(4, flags)) {
            positiveGain = ValueInterpreter.getIntValue(
                bytes,
                ValueInterpreter.FORMAT_UINT16,
                offset
            ) ?: return
            offset += 2

            negativeGain = ValueInterpreter.getIntValue(
                bytes,
                ValueInterpreter.FORMAT_UINT16,
                offset
            ) ?: return
            offset += 2
        }

        if (hasFlag(5, flags)) {
            val rawInstantaneousPace = ValueInterpreter.getIntValue(
                bytes,
                ValueInterpreter.FORMAT_UINT8,
                offset
            ) ?: return

            instantPace = rawInstantaneousPace * 0.1
            offset += 1
        }

        if (hasFlag(6, flags)) {
            val rawAveragePace = ValueInterpreter.getIntValue(
                bytes,
                ValueInterpreter.FORMAT_UINT8,
                offset
            ) ?: return

            averagePace = rawAveragePace * 0.1
            offset += 1
        }

        if (hasFlag(7, flags)) {
            totalEnergyKcal = ValueInterpreter.getIntValue(
                bytes,
                ValueInterpreter.FORMAT_UINT16,
                offset
            )?.toDouble() ?: return
            offset += 2

            energyPerHourKcal = ValueInterpreter.getIntValue(
                bytes,
                ValueInterpreter.FORMAT_UINT16,
                offset
            )?.toDouble() ?: return
            offset += 2

            energyPerMinuteKcal = ValueInterpreter.getIntValue(
                bytes,
                ValueInterpreter.FORMAT_UINT8,
                offset
            )?.toDouble() ?: return
            offset += 1
        }

        if (hasFlag(8, flags)) {
            heartRateBpm = ValueInterpreter.getIntValue(
                bytes,
                ValueInterpreter.FORMAT_UINT8,
                offset
            )?.toDouble() ?: return
            offset += 1
        }

        if (hasFlag(9, flags)) {
            val rawMetabolicEquivalent = ValueInterpreter.getIntValue(
                bytes,
                ValueInterpreter.FORMAT_UINT8,
                offset
            ) ?: return

            metabolicEquivalent = rawMetabolicEquivalent * 0.1
            offset += 1
        }

        if (hasFlag(10, flags)) {
            elapsedTimeSeconds = ValueInterpreter.getIntValue(
                bytes,
                ValueInterpreter.FORMAT_UINT16,
                offset
            )?.toDouble() ?: return
            offset += 2
        }

        if (hasFlag(11, flags)) {
            remainingTimeSeconds = ValueInterpreter.getIntValue(
                bytes,
                ValueInterpreter.FORMAT_UINT16,
                offset
            )?.toDouble() ?: return
            offset += 2
        }

        if (hasFlag(12, flags)) {
            val rawForceOnBelt = ValueInterpreter.getIntValue(
                bytes,
                ValueInterpreter.FORMAT_SINT16,
                offset
            ) ?: return

            forceOnBeltNewtons =
                if (rawForceOnBelt == Short.MAX_VALUE.toInt()) {
                    0.0
                } else {
                    rawForceOnBelt.toDouble()
                }

            offset += 2

            val rawPowerOutput = ValueInterpreter.getIntValue(
                bytes,
                ValueInterpreter.FORMAT_SINT16,
                offset
            ) ?: return

            powerOutputWatts =
                if (rawPowerOutput == Short.MAX_VALUE.toInt()) {
                    0.0
                } else {
                    rawPowerOutput.toDouble()
                }

            offset += 2
        }

        machineData = machineData?.let { current ->
            current.copy(
                instantaneousSpeed =
                    speedMph ?: current.instantaneousSpeed,
                averageSpeed =
                    averageSpeed ?: current.averageSpeed,
                totalDistance =
                    totalDistance ?: current.totalDistance,
                inclination =
                    inclinationPercent ?: current.inclination,
                positiveElevationGain =
                    positiveGain ?: current.positiveElevationGain,
                negativeElevationGain =
                    negativeGain ?: current.negativeElevationGain,
                instantaneousPace =
                    instantPace ?: current.instantaneousPace,
                averagePace =
                    averagePace ?: current.averagePace,
                totalEnergy =
                    totalEnergyKcal ?: current.totalEnergy
            )
        }
    }
}

private fun getUInt24(
    bytes: ByteArray,
    offset: Int
): Int? {
    if (offset + 3 > bytes.size) return null

    return (bytes[offset].toInt() and 0xFF) or
            ((bytes[offset + 1].toInt() and 0xFF) shl 8) or
            ((bytes[offset + 2].toInt() and 0xFF) shl 16)
}

enum class EquipmentType {
    TREADMILL,
    BIKE,
    STAIR_MASTER,
}

sealed interface MachineState {
    data object DetectingMachine: MachineState

    data class TreadmillMachine(
        val instantaneousSpeed: Double?,
        val heartRate: Int?,
        val distance: Double = 0.0,
        val totalDistance: Int? = 0,
    ): MachineState
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

fun createMachine(equipmentType: EquipmentType): Machine<*> {
    return when (equipmentType) {
        EquipmentType.TREADMILL -> Treadmill()
        EquipmentType.BIKE -> TODO()
        EquipmentType.STAIR_MASTER -> TODO()
    }
}