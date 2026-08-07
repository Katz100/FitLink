package com.hopkins.fitlink.core.room.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import com.hopkins.fitlink.core.room.RoomConstants
import com.hopkins.fitlink.core.room.entity.TreadmillMetricsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TreadmillDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTreadmillMetric(treadmillMetricsEntity: TreadmillMetricsEntity)

    @Query(
        """
            SELECT * FROM ${RoomConstants.TREADMILL_TABLE}
        """
    )
    fun getAllTreadmillMetrics(): Flow<List<TreadmillMetricsEntity>>
}