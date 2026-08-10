package com.hopkins.fitlink.nav

import kotlinx.serialization.Serializable

sealed interface Screen {
    companion object {
        val BottomDestinations = listOf(
            Home,
            History,
            Settings
        )
    }

    @Serializable
    object Home: Screen

    @Serializable
    object History: Screen

    @Serializable
    object Settings: Screen

    @Serializable
    data class ActiveWorkout(val macAddress: String): Screen
}
