package com.hopkins.fitlink.core.data

import com.hopkins.fitlink.core.room.entity.TreadmillSessionDomain
import kotlinx.coroutines.flow.Flow

interface WorkoutRepository {
    fun getAllTreadmillSessions(): Flow<List<TreadmillSessionDomain>>
    suspend fun insertTreadmillSession(treadmillSession: TreadmillSessionDomain)
}