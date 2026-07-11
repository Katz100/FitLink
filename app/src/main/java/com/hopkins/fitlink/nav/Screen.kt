package com.hopkins.fitlink.nav

import kotlinx.serialization.Serializable

sealed interface Screen {
    @Serializable
    object Home: Screen

    @Serializable
    object Workout: Screen
}