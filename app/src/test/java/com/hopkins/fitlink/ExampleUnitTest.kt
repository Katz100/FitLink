package com.hopkins.fitlink

import com.hopkins.fitlink.Workout.hasFlag
import com.polidea.rxandroidble3.helpers.ValueInterpreter
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val bytes = byteArrayOf(
            0x00.toByte(),
            0x1F.toByte(),
            0xC3.toByte(),
            0x01.toByte(),
            0x00.toByte(),
            0x07.toByte(),
            0x24.toByte(),
            0x00.toByte(),
            0xE4.toByte(),
            0x06.toByte(),
            0xFF.toByte(),
            0x7F.toByte(),
            0x7D.toByte(),
            0x00.toByte()
        )

        val flags = ValueInterpreter.getIntValue(
            bytes,
            ValueInterpreter.FORMAT_UINT16,
            0,
        )

        val instant = ValueInterpreter.getIntValue(
            bytes,
            ValueInterpreter.FORMAT_UINT16,
            2
        )

        if (!hasFlag(0, flags)) {
            println("Has bit")
        }
        println("TESTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT")
        println(instant)

        assert(true)
    }
}