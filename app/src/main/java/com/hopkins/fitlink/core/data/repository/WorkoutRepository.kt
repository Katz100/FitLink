package com.hopkins.fitlink.core.data.repository

import com.hopkins.fitlink.core.data.room.entity.TreadmillSessionDomain
import kotlinx.coroutines.flow.Flow

interface WorkoutRepository {
    fun getAllTreadmillSessions(): Flow<List<TreadmillSessionDomain>>
    suspend fun insertTreadmillSession(treadmillSession: TreadmillSessionDomain)
    suspend fun getMostRecentTreadmillSession(): TreadmillSessionDomain
}