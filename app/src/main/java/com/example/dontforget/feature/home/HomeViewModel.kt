package com.example.dontforget.feature.home

import androidx.lifecycle.viewModelScope
import com.example.dontforget.core.mvi.BaseViewModel
import com.example.dontforget.data.repository.ReminderRepository
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class HomeViewModel(
    private val reminderRepository: ReminderRepository
) : BaseViewModel<HomeUiState, HomeUiEvent, HomeUiSideEffect>(HomeUiState()) {

    init {
        observeActiveReminders()
        observeCompletedReminders()
    }

    private fun observeActiveReminders() {
        reminderRepository.getActiveReminders()
            .onEach { active ->
                updateState {
                    copy(
                        isLoading = false,
                        activeReminders = active
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun observeCompletedReminders() {
        reminderRepository.getCompletedReminders()
            .onEach { completed ->
                updateState {
                    copy(
                        completedReminders = completed
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    override fun onEvent(event: HomeUiEvent) {
        when (event) {
            is HomeUiEvent.OnReminderClick -> {
                sendEffect(HomeUiSideEffect.NavigateToDetail(event.reminderId))
            }
            is HomeUiEvent.OnFilterTabSelected -> {
                updateState { copy(selectedFilter = event.filterTab) }
            }
            HomeUiEvent.OnCreateReminderClick -> {
                sendEffect(HomeUiSideEffect.NavigateToCreateReminder)
            }
            HomeUiEvent.OnOpenNotificationSettingsClick -> {
                sendEffect(HomeUiSideEffect.OpenNotificationSettings)
            }
            HomeUiEvent.OnNotificationIconClick -> {
                // Mock notification action (no-op)
            }
            is HomeUiEvent.OnNotificationPermissionStatusChanged -> {
                updateState { copy(isNotificationPermissionGranted = event.isGranted) }
            }
        }
    }
}
