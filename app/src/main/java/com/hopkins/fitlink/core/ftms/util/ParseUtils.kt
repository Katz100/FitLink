package com.hopkins.fitlink.core.ftms.util

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