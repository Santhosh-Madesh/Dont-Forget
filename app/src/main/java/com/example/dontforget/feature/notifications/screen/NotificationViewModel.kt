package com.example.dontforget.feature.notifications.screen

import androidx.lifecycle.viewModelScope
import com.example.dontforget.core.mvi.BaseViewModel
import com.example.dontforget.data.local.model.ReminderWithItems
import com.example.dontforget.data.repository.ReminderRepository
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class NotificationViewModel(
    private val reminderRepository: ReminderRepository
) : BaseViewModel<NotificationUiState, NotificationUiEvent, NotificationUiSideEffect>(
    initialState = NotificationUiState()
) {

    init {
        observeReminders()
    }

    private fun observeReminders() {
        combine(
            reminderRepository.getActiveReminders(),
            reminderRepository.getCompletedReminders()
        ) { active, completed ->
            active + completed
        }.onEach { allReminders ->
            val currentTime = System.currentTimeMillis()
            val notificationItems = allReminders.map { reminderWithItems ->
                toNotificationItem(reminderWithItems, currentTime)
            }

            val upcoming = notificationItems
                .filter { !it.isTriggered }
                .sortedBy { it.triggerEpochMillis }

            val previous = notificationItems
                .filter { it.isTriggered }
                .sortedByDescending { it.triggerEpochMillis }

            updateState {
                copy(
                    isLoading = false,
                    upcomingNotifications = upcoming,
                    previousNotifications = previous
                )
            }
        }.launchIn(viewModelScope)
    }

    private fun toNotificationItem(
        reminderWithItems: ReminderWithItems,
        currentTime: Long
    ): NotificationItem {
        val reminder = reminderWithItems.reminder
        val leadMillis = reminder.notificationLeadMinutes * 60 * 1000L
        val triggerEpochMillis = reminder.departureEpochMillis - leadMillis
        val isTriggered = triggerEpochMillis <= currentTime

        return NotificationItem(
            reminderId = reminder.id,
            title = reminder.contextTitle,
            content = "Reminder: ${reminder.contextTitle}",
            triggerEpochMillis = triggerEpochMillis,
            departureEpochMillis = reminder.departureEpochMillis,
            isTriggered = isTriggered
        )
    }

    override fun onEvent(event: NotificationUiEvent) {
        when (event) {
            NotificationUiEvent.OnBackClick -> {
                sendEffect(NotificationUiSideEffect.NavigateBack)
            }
        }
    }
}
