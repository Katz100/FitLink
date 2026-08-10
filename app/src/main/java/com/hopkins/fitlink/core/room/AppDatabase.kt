package com.hopkins.fitlink.core.room

import androidx.room3.Database
import androidx.room3.RoomDatabase
import com.hopkins.fitlink.core.room.dao.TreadmillDao
import com.hopkins.fitlink.core.room.entity.TreadmillSessionEntity

@Database(entities = [TreadmillSessionEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun treadmillDao(): TreadmillDao
}