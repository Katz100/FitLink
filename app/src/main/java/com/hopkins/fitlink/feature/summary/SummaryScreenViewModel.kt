package com.hopkins.fitlink.feature.summary

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.hopkins.fitlink.core.data.repository.WorkoutRepository
import com.hopkins.fitlink.core.domain.model.EquipmentType
import com.hopkins.fitlink.nav.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SummaryScreenViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    companion object {
        private const val TAG = "SummaryScreenVM"
    }
    val sessionId = savedStateHandle.toRoute<Screen.WorkoutSummary>().id
    val equipmentType = savedStateHandle.toRoute<Screen.WorkoutSummary>().equipmentType

    private val _uiState = MutableStateFlow(SummaryUiState())
    val uiState: StateFlow<SummaryUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.collect {
                Timber.tag(TAG).i("$it")
            }
        }
        when (equipmentType) {
            EquipmentType.TREADMILL -> {
                viewModelScope.launch(Dispatchers.IO) {
                    _uiState.update {
                        it.copy(
                            result = workoutRepository.getTreadmillSessionById(sessionId)
                        )
                    }
                }
            }
            else -> {
                Timber.tag(TAG).i("Received unsupported equipment type.")
            }
        }
    }
}