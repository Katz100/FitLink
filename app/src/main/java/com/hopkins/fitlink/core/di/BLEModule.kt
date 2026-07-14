package com.hopkins.fitlink.core.di

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import com.hopkins.fitlink.core.ble.FitBLE
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class BLEModule {

    @Singleton
    @Provides
    fun provideBluetoothAdapter(
        @ApplicationContext context: Context
    ): BluetoothAdapter? {
        val bluetoothManager: BluetoothManager = context.getSystemService(BluetoothManager::class.java)
        return bluetoothManager.adapter
    }

    @Singleton
    @Provides
    fun provideFitBle(
        bluetoothAdapter: BluetoothAdapter?
    ): FitBLE = FitBLE(bluetoothAdapter)
}