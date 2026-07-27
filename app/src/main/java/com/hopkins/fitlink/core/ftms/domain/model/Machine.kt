package com.hopkins.fitlink.core.ftms.domain.model

abstract class Machine<Data>(
) {
    abstract var machineData: Data?

    abstract fun parseDataForMachine(bytes: ByteArray)
}