package com.hopkins.fitlink.Workout

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import com.hopkins.fitlink.core.data.BleRepository
import com.hopkins.fitlink.core.ftms.FTMSConstants
import com.hopkins.fitlink.nav.Screen
import com.polidea.rxandroidble3.helpers.ValueInterpreter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class WorkoutScreenViewModel @Inject constructor(
    bleRepository: BleRepository,
    savedStateHandle: SavedStateHandle,
): ViewModel() {
    private val deviceAddress = savedStateHandle.toRoute<Screen.ActiveWorkout>().macAddress

    private val _speed = MutableStateFlow<Double>(0.0)
    val speed = _speed.asStateFlow()

    init {
        bleRepository.connectAndSubscribeToCharacteristic(
            deviceAddress = deviceAddress,
            characteristic = UUID.fromString(FTMSConstants.TREADMILL_CHARACTERISTIC),
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
            onNotificationChanged = {}
        )
    }
}

fun hasFlag(bit: Int, flags: Int): Boolean {
    return flags and (1 shl bit) != 0
}