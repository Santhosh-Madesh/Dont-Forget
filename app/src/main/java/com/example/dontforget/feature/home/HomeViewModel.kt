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
        observeReminders()
    }

    private fun observeReminders() {
        reminderRepository.getActiveReminders()
            .onEach { reminders ->
                updateState {
                    copy(
                        isLoading = false,
                        reminders = reminders
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
            HomeUiEvent.OnCreateReminderClick -> {
                sendEffect(HomeUiSideEffect.NavigateToCreateReminder)
            }
            HomeUiEvent.OnTemplatesClick -> {
                sendEffect(HomeUiSideEffect.NavigateToTemplates)
            }
            HomeUiEvent.OnOpenNotificationSettingsClick -> {
                sendEffect(HomeUiSideEffect.OpenNotificationSettings)
            }
        }
    }
}
