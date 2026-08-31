package com.hopkins.fitlink.core.data.repository

import com.hopkins.fitlink.core.data.room.entity.TreadmillSessionDomain
import com.hopkins.fitlink.core.data.room.entity.TreadmillSessionEntity
import com.hopkins.fitlink.feature.summary.WorkoutResult
import kotlinx.coroutines.flow.Flow

interface WorkoutRepository {
    fun getAllTreadmillSessions(): Flow<List<TreadmillSessionDomain>>
    suspend fun insertTreadmillSession(treadmillSession: TreadmillSessionDomain): Long
    suspend fun getMostRecentTreadmillSession(): TreadmillSessionDomain
    suspend fun getTreadmillSessionById(id: Long): WorkoutResult
}