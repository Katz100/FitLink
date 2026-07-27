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
