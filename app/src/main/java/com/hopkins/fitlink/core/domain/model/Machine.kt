package com.hopkins.fitlink.core.domain.model

abstract class Machine<Data>(
) {
    abstract var machineData: Data?

    abstract fun parseDataForMachine(bytes: ByteArray)
}