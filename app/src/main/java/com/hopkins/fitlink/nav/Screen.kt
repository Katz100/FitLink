package com.hopkins.fitlink.nav

import kotlinx.serialization.Serializable

sealed interface Screen {
    @Serializable
    object Home: Screen

    @Serializable
    data class ActiveWorkout(val macAddress: String): Screen
}