package com.hopkins.fitlink.core.domain.model

sealed interface NotificationChanged {
    data object NotificationLoading: NotificationChanged
    data object NotificationCreated: NotificationChanged
    data object NotificationEnded: NotificationChanged
    data class NotificationError(val e: Throwable): NotificationChanged
}