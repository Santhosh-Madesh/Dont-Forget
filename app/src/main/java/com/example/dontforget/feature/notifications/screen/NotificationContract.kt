package com.example.dontforget.feature.notifications.screen

import com.example.dontforget.core.mvi.UiEvent
import com.example.dontforget.core.mvi.UiSideEffect
import com.example.dontforget.core.mvi.UiState

data class NotificationItem(
    val reminderId: Long,
    val title: String,
    val content: String,
    val triggerEpochMillis: Long,
    val departureEpochMillis: Long,
    val isTriggered: Boolean
)

data class NotificationUiState(
    val isLoading: Boolean = true,
    val upcomingNotifications: List<NotificationItem> = emptyList(),
    val previousNotifications: List<NotificationItem> = emptyList()
) : UiState {
    val upcomingCount: Int get() = upcomingNotifications.size
    val previousCount: Int get() = previousNotifications.size
    val isEmpty: Boolean get() = !isLoading && upcomingNotifications.isEmpty() && previousNotifications.isEmpty()
}

sealed interface NotificationUiEvent : UiEvent {
    object OnBackClick : NotificationUiEvent
}

sealed interface NotificationUiSideEffect : UiSideEffect {
    object NavigateBack : NotificationUiSideEffect
}
