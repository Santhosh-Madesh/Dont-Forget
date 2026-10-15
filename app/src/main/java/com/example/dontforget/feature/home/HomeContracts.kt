package com.example.dontforget.feature.home

import com.example.dontforget.core.mvi.UiEvent
import com.example.dontforget.core.mvi.UiSideEffect
import com.example.dontforget.core.mvi.UiState
import com.example.dontforget.data.local.model.ReminderWithItems
import java.util.Calendar

enum class HomeFilterTab {
    ACTIVE,
    COMPLETED
}

data class HomeUiState(
    val isLoading: Boolean = true,
    val selectedFilter: HomeFilterTab = HomeFilterTab.ACTIVE,
    val activeReminders: List<ReminderWithItems> = emptyList(),
    val completedReminders: List<ReminderWithItems> = emptyList(),
    val isNotificationPermissionGranted: Boolean = true
) : UiState {

    val todayReminders: List<ReminderWithItems>
        get() = activeReminders.filter { isToday(it.reminder.departureEpochMillis) }

    val tomorrowReminders: List<ReminderWithItems>
        get() = activeReminders.filter { isTomorrow(it.reminder.departureEpochMillis) }

    val upcomingReminders: List<ReminderWithItems>
        get() = activeReminders.filter { isUpcoming(it.reminder.departureEpochMillis) }

    val isCurrentViewEmpty: Boolean
        get() = when (selectedFilter) {
            HomeFilterTab.ACTIVE -> activeReminders.isEmpty()
            HomeFilterTab.COMPLETED -> completedReminders.isEmpty()
        }
}

sealed interface HomeUiEvent : UiEvent {
    data class OnReminderClick(val reminderId: Long) : HomeUiEvent
    data class OnFilterTabSelected(val filterTab: HomeFilterTab) : HomeUiEvent
    object OnCreateReminderClick : HomeUiEvent
    object OnOpenNotificationSettingsClick : HomeUiEvent
    object OnNotificationIconClick : HomeUiEvent
    data class OnNotificationPermissionStatusChanged(val isGranted: Boolean) : HomeUiEvent
}

sealed interface HomeUiSideEffect : UiSideEffect {
    data class NavigateToDetail(val reminderId: Long) : HomeUiSideEffect
    object NavigateToCreateReminder : HomeUiSideEffect
    object OpenNotificationSettings : HomeUiSideEffect
}

fun isToday(epochMillis: Long): Boolean {
    val calNow = Calendar.getInstance()
    val calTarget = Calendar.getInstance().apply { timeInMillis = epochMillis }
    return calNow.get(Calendar.YEAR) == calTarget.get(Calendar.YEAR) &&
            calNow.get(Calendar.DAY_OF_YEAR) == calTarget.get(Calendar.DAY_OF_YEAR)
}

fun isTomorrow(epochMillis: Long): Boolean {
    val calTomorrow = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }
    val calTarget = Calendar.getInstance().apply { timeInMillis = epochMillis }
    return calTomorrow.get(Calendar.YEAR) == calTarget.get(Calendar.YEAR) &&
            calTomorrow.get(Calendar.DAY_OF_YEAR) == calTarget.get(Calendar.DAY_OF_YEAR)
}

fun isUpcoming(epochMillis: Long): Boolean {
    val calTomorrow = Calendar.getInstance().apply {
        add(Calendar.DAY_OF_YEAR, 1)
        set(Calendar.HOUR_OF_DAY, 23)
        set(Calendar.MINUTE, 59)
        set(Calendar.SECOND, 59)
        set(Calendar.MILLISECOND, 999)
    }
    return epochMillis > calTomorrow.timeInMillis
}
