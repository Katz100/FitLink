package com.hopkins.fitlink.feature.workout

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import com.hopkins.fitlink.core.data.BleRepository
import com.hopkins.fitlink.core.data.ConnectionStatus
import com.hopkins.fitlink.core.data.NotificationChanged
import com.hopkins.fitlink.core.ftms.EquipmentType
import com.hopkins.fitlink.core.ftms.FTMSConstants
import com.hopkins.fitlink.nav.Screen
import com.polidea.rxandroidble3.helpers.ValueInterpreter
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class WorkoutScreenViewModel @Inject constructor(
    private val bleRepository: BleRepository,
    savedStateHandle: SavedStateHandle,
): ViewModel() {
    private val deviceAddress = savedStateHandle.toRoute<Screen.ActiveWorkout>().macAddress


    private val _speed = MutableStateFlow<Double>(0.0)
    val speed = _speed.asStateFlow()

    private val _equipmentType = MutableStateFlow<EquipmentType>(EquipmentType.TREADMILL)
    val equipmentType = _equipmentType.asStateFlow()

    private val _notificationStatus = MutableStateFlow<NotificationChanged>(NotificationChanged.NotificationLoading)
    val notificationStatus = _notificationStatus.asStateFlow()

    init {
        connectToDevice()
    }

    private fun connectToDevice() {
        bleRepository.connectToDevice(
            deviceAddress = deviceAddress,
            connectionStatusChanged = {
                if (it is ConnectionStatus.Connected) {
                    discoverCharacteristics()
                }
            }
        )
    }

    private fun discoverCharacteristics() {
        bleRepository.discoverCharacteristic(
            deviceAddress = deviceAddress,
            onEquipmentCharacteristicFound = { equipmentType ->
                _equipmentType.value = equipmentType
            },
            onFinished = {
                val characteristic = when(_equipmentType.value) {
                    EquipmentType.TREADMILL -> UUID.fromString(FTMSConstants.TREADMILL_CHARACTERISTIC)
                    EquipmentType.BIKE -> TODO()
                    EquipmentType.STAIR_MASTER -> TODO()
                }

                subscribeToCharacteristic(deviceAddress, characteristic)
            }
        )
    }
    fun updateSpeed() {
        bleRepository.setSpeed(500.0, deviceAddress)
    }
    private fun subscribeToCharacteristic(
        deviceAddress: String,
        characteristicUUID: UUID
    ) {
        bleRepository.connectAndSubscribeToCharacteristic(
            deviceAddress = deviceAddress,
            characteristic = characteristicUUID,
            onBytesReceived = { bytes ->
                val flags = ValueInterpreter.getIntValue(
                    bytes,
                    ValueInterpreter.FORMAT_UINT16,
                    0,
                )?: return@connectAndSubscribeToCharacteristic

                if (hasFlag(0, flags)) return@connectAndSubscribeToCharacteristic

                val instant = ValueInterpreter.getIntValue(
                    bytes,
                    ValueInterpreter.FORMAT_UINT16,
                    2
                )?: return@connectAndSubscribeToCharacteristic

                val speedKph = instant / 100.0
                val speedMph = speedKph * 0.621371

                _speed.value = speedMph
            },
            onNotificationChanged = { notification ->
                _notificationStatus.value = notification
            }
        )
    }
}

fun hasFlag(bit: Int, flags: Int): Boolean {
    return flags and (1 shl bit) != 0
}