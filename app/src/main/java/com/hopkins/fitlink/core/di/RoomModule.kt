package com.hopkins.fitlink.core.di

import android.content.Context
import androidx.room3.Room
import androidx.sqlite.driver.AndroidSQLiteDriver
import com.hopkins.fitlink.core.room.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object RoomModule {
    @Singleton
    @Provides
    fun provideRoomDatabase(
        @ApplicationContext context: Context,
    ): AppDatabase = Room.databaseBuilder<AppDatabase>(context, "fit-link")
                .setDriver(AndroidSQLiteDriver())
                .build()
}