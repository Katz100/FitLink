package com.hopkins.fitlink.core.data.room.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import com.hopkins.fitlink.core.data.room.RoomConstants
import com.hopkins.fitlink.core.data.room.entity.TreadmillSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TreadmillDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTreadmillSession(treadmillSessionEntity: TreadmillSessionEntity): Long

    @Query(
        """
            SELECT * FROM ${RoomConstants.TREADMILL_TABLE}
        """
    )
    fun getAllTreadmillSessions(): Flow<List<TreadmillSessionEntity>>

    @Query(
        """
            SELECT * FROM ${RoomConstants.TREADMILL_TABLE}
            ORDER BY timestamp desc
            LIMIT 1
        """
    )
    suspend fun getMostRecentTreadmillSession(): TreadmillSessionEntity

    @Query(
        """
            SELECT * FROM ${RoomConstants.TREADMILL_TABLE}
            WHERE id = :id
        """
    )
    suspend fun getTreadmillSessionById(id: Long): TreadmillSessionEntity?
}