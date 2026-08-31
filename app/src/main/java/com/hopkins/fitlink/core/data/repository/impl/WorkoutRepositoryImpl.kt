package com.hopkins.fitlink.core.data.repository.impl

import com.hopkins.fitlink.core.data.repository.WorkoutRepository
import com.hopkins.fitlink.core.data.room.dao.TreadmillDao
import com.hopkins.fitlink.core.data.room.entity.TreadmillSessionDomain
import com.hopkins.fitlink.core.data.room.entity.toDomain
import com.hopkins.fitlink.core.data.room.entity.toEntity
import com.hopkins.fitlink.feature.summary.WorkoutResult
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

    override suspend fun insertTreadmillSession(treadmillSession: TreadmillSessionDomain): Long {
        return treadmillDao.insertTreadmillSession(treadmillSession.toEntity())
    }

    override suspend fun getMostRecentTreadmillSession(): TreadmillSessionDomain {
        return treadmillDao.getMostRecentTreadmillSession().toDomain()
    }

    override suspend fun getTreadmillSessionById(id: Long): WorkoutResult {
        val result = treadmillDao.getTreadmillSessionById(id)?.toDomain()
        if (result != null) return WorkoutResult.Success(result)
        return WorkoutResult.Error("Session not found.")
    }
}