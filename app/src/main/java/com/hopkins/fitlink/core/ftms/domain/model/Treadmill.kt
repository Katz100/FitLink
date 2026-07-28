package com.hopkins.fitlink.core.ftms.domain.model

import com.hopkins.fitlink.core.ftms.FTMSConstants
import com.hopkins.fitlink.core.ftms.util.getUInt24
import com.hopkins.fitlink.core.ftms.util.hasFlag
import com.polidea.rxandroidble3.helpers.ValueInterpreter

class Treadmill: Machine<TreadmillData>(
) {
    override var machineData: TreadmillData? = TreadmillData(
        moreData = false,
        instantaneousSpeed = null,
        averageSpeed = null,
        totalDistance = null,
        inclination = null,
        rampAngleSetting = null,
        positiveElevationGain = null,
        negativeElevationGain = null,
        instantaneousPace = null,
        averagePace = null,
        totalEnergy = null,
        energyPerHour = null,
        energyPerMinute = null,
        heartRate = null,
        metabolicEquivalent = null,
        elapsedTime = null,
        remainingTime = null,
        forceOnBelt = null,
        powerOutput = null
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
        var heartRateBpm: Int? = null
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

            inclinationPercent =
                if (rawInclination == Short.MAX_VALUE.toInt()) {
                    null
                } else {
                    rawInclination * 0.1
                }
            offset += 2

            val rawRampAngle = ValueInterpreter.getIntValue(
                bytes,
                ValueInterpreter.FORMAT_SINT16,
                offset
            ) ?: return

            inclinationAngle =
                if (rawRampAngle == Short.MAX_VALUE.toInt()) {
                    null
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
            ) ?: return
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
                    null
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
                    null
                } else {
                    rawPowerOutput.toDouble()
                }

            offset += 2
        }

        machineData = machineData?.let { current ->
            current.copy(
                heartRate = heartRateBpm ?: current.heartRate,
                elapsedTime = elapsedTimeSeconds?.toInt() ?: current.elapsedTime,
                remainingTime = remainingTimeSeconds?.toInt() ?: current.remainingTime,
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
                    totalEnergyKcal ?: current.totalEnergy,
            )
        }
    }
}