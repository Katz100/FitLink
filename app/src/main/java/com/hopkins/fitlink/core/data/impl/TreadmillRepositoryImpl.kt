package com.hopkins.fitlink.core.data.impl

import com.hopkins.fitlink.core.data.TreadmillRepository
import com.hopkins.fitlink.core.room.dao.TreadmillDao
import javax.inject.Inject

class TreadmillRepositoryImpl @Inject constructor(
    treadmillDao: TreadmillDao
) : TreadmillRepository {

}