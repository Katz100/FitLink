package com.hopkins.fitlink.core.data.repository.impl

import com.hopkins.fitlink.core.data.repository.WorkoutRepository
import com.hopkins.fitlink.core.data.room.dao.TreadmillDao
import com.hopkins.fitlink.core.data.room.entity.TreadmillSessionDomain
import com.hopkins.fitlink.core.data.room.entity.toDomain
import com.hopkins.fitlink.core.data.room.entity.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WorkoutRepositoryImpl @Inject constructor(
    private val treadmillDao: TreadmillDao
) : WorkoutRepository {
    override fun getAllTreadmillSessions(): Flow<List<TreadmillSessionDomain>> {
        return treadmillDao.getAllTreadmillSessions().map { entityList ->
            entityList.map { entity ->
                entity.toDomain()
            }
        }
    }

    override suspend fun insertTreadmillSession(treadmillSession: TreadmillSessionDomain) {
        treadmillDao.insertTreadmillSession(treadmillSession.toEntity())
    }
}