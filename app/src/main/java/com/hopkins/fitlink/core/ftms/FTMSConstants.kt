package com.hopkins.fitlink.core.ftms

object FTMSConstants {
    const val FTMS_MACHINE = "00001826-0000-1000-8000-00805f9b34fb"
    const val CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb"
    const val TREADMILL_CHARACTERISTIC = "00002acd-0000-1000-8000-00805f9b34fb"
    const val MASK = 0xFF

    fun parseSpeed(data: ByteArray): Double {
        var speed = 0.0
        var offset = 0
        val flags = getRawData16(offset, data)
        offset += 2

        val instant = flags and 0x0001 == 0

        if (instant) {
            val rawSpeed = getRawData16(offset, data)
            speed = ((rawSpeed / 100) * 0.621371)
        }
        return speed
    }

    fun getRawData16(offset: Int, data: ByteArray): Int {
        return data[offset].toInt() and MASK or ((data[offset + 1].toInt() and MASK) shl 8)
    }
}