package com.hopkins.fitlink.core.data

import com.hopkins.fitlink.core.room.entity.TreadmillSessionDomain
import kotlinx.coroutines.flow.Flow

interface TreadmillRepository {
    fun getAllTreadmillMetrics(): Flow<List<TreadmillSessionDomain>>
}