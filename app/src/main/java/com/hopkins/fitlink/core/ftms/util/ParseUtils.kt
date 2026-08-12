package com.hopkins.fitlink.core.ftms.util

import com.hopkins.fitlink.core.ftms.FTMSConstants
import com.hopkins.fitlink.feature.workout.FitnessMachineStatus
import com.polidea.rxandroidble3.helpers.ValueInterpreter
import com.polidea.rxandroidble3.helpers.ValueInterpreter.FORMAT_UINT8

fun getUInt24(
    bytes: ByteArray,
    offset: Int
): Int? {
    if (offset + 3 > bytes.size) return null

    return (bytes[offset].toInt() and 0xFF) or
            ((bytes[offset + 1].toInt() and 0xFF) shl 8) or
            ((bytes[offset + 2].toInt() and 0xFF) shl 16)
}

fun hasFlag(bit: Int, flags: Int): Boolean {
    return flags and (1 shl bit) != 0
}

fun formatSeconds(seconds: Int?): String {
    if (seconds == null || seconds < 0) return "--:--"

    val minutes = seconds / 60
    val remainingSeconds = seconds % 60

    return "%02d:%02d".format(minutes, remainingSeconds)
}

fun parseFitnessMachineStatus(bytes: ByteArray): FitnessMachineStatus {
    var offset = 0
    var op = ValueInterpreter.getIntValue(
        bytes,
        FORMAT_UINT8,
        offset
    )
    if (op == FTMSConstants.FMS_RESET) return FitnessMachineStatus.Reset
    if (op == FTMSConstants.FMS_STOPPED_BY_SAFETY_KEY) return FitnessMachineStatus.StoppedBySafetyKey
    if (op == FTMSConstants.FMS_RESUMED) return FitnessMachineStatus.Started

    if (op == FTMSConstants.FMS_STOPPED_OR_PAUSED) {
        offset++
        op = ValueInterpreter.getIntValue(
            bytes,
            FORMAT_UINT8,
            offset
        )
        if (op == FTMSConstants.FMS_PAUSED) return FitnessMachineStatus.Paused
        if (op == FTMSConstants.FMS_STOPPED) return FitnessMachineStatus.Stopped
    }
    return FitnessMachineStatus.Unknown
}