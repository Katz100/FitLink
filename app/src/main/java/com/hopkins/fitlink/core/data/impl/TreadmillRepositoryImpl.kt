package com.hopkins.fitlink.core.data.impl

import com.hopkins.fitlink.core.data.TreadmillRepository
import com.hopkins.fitlink.core.room.dao.TreadmillDao
import com.hopkins.fitlink.core.room.entity.TreadmillSessionDomain
import com.hopkins.fitlink.core.room.entity.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TreadmillRepositoryImpl @Inject constructor(
    private val treadmillDao: TreadmillDao
) : TreadmillRepository {
    override fun getAllTreadmillMetrics(): Flow<List<TreadmillSessionDomain>> {
        return treadmillDao.getAllTreadmillMetrics().map { entityList ->
            entityList.map { entity ->
                entity.toDomain()
            }
        }
    }
}