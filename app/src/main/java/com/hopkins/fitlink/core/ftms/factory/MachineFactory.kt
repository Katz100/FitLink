package com.hopkins.fitlink.core.ftms.factory

import com.hopkins.fitlink.core.domain.model.EquipmentType
import com.hopkins.fitlink.core.domain.model.Machine
import com.hopkins.fitlink.core.domain.model.Treadmill

fun createMachine(equipmentType: EquipmentType): Machine<*> {
    return when (equipmentType) {
        EquipmentType.TREADMILL -> Treadmill()
        EquipmentType.BIKE -> TODO()
        EquipmentType.STAIR_MASTER -> TODO()
    }
}