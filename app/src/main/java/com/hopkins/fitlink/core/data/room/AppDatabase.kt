package com.hopkins.fitlink.core.data.room

import androidx.room3.Database
import androidx.room3.RoomDatabase
import com.hopkins.fitlink.core.data.room.dao.TreadmillDao
import com.hopkins.fitlink.core.data.room.entity.TreadmillSessionEntity

@Database(entities = [TreadmillSessionEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun treadmillDao(): TreadmillDao
}