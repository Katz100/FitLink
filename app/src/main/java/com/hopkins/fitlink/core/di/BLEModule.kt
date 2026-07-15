package com.hopkins.fitlink.core.di

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import com.hopkins.fitlink.core.ble.FitBluetoothLeScanner
import com.hopkins.fitlink.core.ble.FitBluetoothScannerImpl
import com.hopkins.fitlink.core.data.BleRepository
import com.hopkins.fitlink.core.data.impl.BleRepositoryImpl
import com.polidea.rxandroidble3.RxBleClient
import dagger.Binds
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
    fun provideRxBleClient(
        @ApplicationContext context: Context,
    ): RxBleClient {
        return RxBleClient.create(context)
    }

    @Singleton
    @Provides
    fun provideBluetoothAdapter(
        @ApplicationContext context: Context
    ): BluetoothAdapter? {
        val bluetoothManager: BluetoothManager = context.getSystemService(BluetoothManager::class.java)
        return bluetoothManager.adapter
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class BluetoothScannerBindModule {

    @Binds
    abstract fun bindBleRepository(
        impl: BleRepositoryImpl
    ): BleRepository

    @Binds
    @Singleton
    abstract fun bindFitBluetoothLeScanner(
        impl: FitBluetoothScannerImpl
    ): FitBluetoothLeScanner
}