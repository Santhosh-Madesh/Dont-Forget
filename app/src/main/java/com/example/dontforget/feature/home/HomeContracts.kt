package com.example.dontforget.feature.home

import com.example.dontforget.core.mvi.UiEvent
import com.example.dontforget.core.mvi.UiSideEffect
import com.example.dontforget.core.mvi.UiState
import com.example.dontforget.data.local.model.ReminderWithItems

data class HomeUiState(
    val isLoading: Boolean = true,
    val reminders: List<ReminderWithItems> = emptyList(),
    val isNotificationPermissionGranted: Boolean = true
) : UiState

sealed interface HomeUiEvent : UiEvent {
    data class OnReminderClick(val reminderId: Long) : HomeUiEvent
    object OnCreateReminderClick : HomeUiEvent
    object OnTemplatesClick : HomeUiEvent
    object OnOpenNotificationSettingsClick : HomeUiEvent
}

sealed interface HomeUiSideEffect : UiSideEffect {
    data class NavigateToDetail(val reminderId: Long) : HomeUiSideEffect
    object NavigateToCreateReminder : HomeUiSideEffect
    object NavigateToTemplates : HomeUiSideEffect
    object OpenNotificationSettings : HomeUiSideEffect
}
