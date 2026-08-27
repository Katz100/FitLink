package com.hopkins.fitlink.core.domain.model

sealed interface ConnectionStatus {
    data object Disconnected: ConnectionStatus
    data object ConnectionLoading: ConnectionStatus
    data object Connected: ConnectionStatus
    data class ConnectionError(val e: Throwable): ConnectionStatus
}