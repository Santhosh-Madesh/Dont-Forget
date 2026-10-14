package com.example.dontforget.feature.reminderdetail

import androidx.lifecycle.viewModelScope
import com.example.dontforget.core.mvi.BaseViewModel
import com.example.dontforget.data.repository.ReminderRepository
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class ReminderDetailViewModel(
    private val reminderId: Long,
    private val reminderRepository: ReminderRepository
) : BaseViewModel<ReminderDetailUiState, ReminderDetailUiEvent, ReminderDetailUiSideEffect>(ReminderDetailUiState()) {

    init {
        observeReminder()
    }

    private fun observeReminder() {
        reminderRepository.getReminderById(reminderId)
            .onEach { reminderWithItems ->
                if (reminderWithItems != null) {
                    updateState {
                        copy(
                            isLoading = false,
                            reminderWithItems = reminderWithItems,
                            localItems = reminderWithItems.itemsFiltered,
                            isCompleted = reminderWithItems.reminder.isCompleted
                        )
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    override fun onEvent(event: ReminderDetailUiEvent) {
        when (event) {
            is ReminderDetailUiEvent.OnToggleItemPacked -> {
                val updated = currentState.localItems.map { item ->
                    if (item.id == event.itemId) item.copy(isPacked = event.isPacked) else item
                }
                updateState { copy(localItems = updated, restoreWarning = null) }
                viewModelScope.launch {
                    reminderRepository.updateChecklistItemPacked(event.itemId, event.isPacked)
                }
            }
            ReminderDetailUiEvent.OnPackAllClick -> {
                val updated = currentState.localItems.map { it.copy(isPacked = true) }
                updateState { copy(localItems = updated) }
                viewModelScope.launch {
                    reminderRepository.packAllItems(reminderId)
                }
            }
            ReminderDetailUiEvent.OnUpdateClick -> {
                sendEffect(ReminderDetailUiSideEffect.ShowSnackbar("Checklist updated"))
            }
            ReminderDetailUiEvent.OnMarkCompleteClick -> {
                viewModelScope.launch {
                    reminderRepository.markReminderComplete(reminderId)
                    sendEffect(ReminderDetailUiSideEffect.NavigateBack)
                }
            }
            ReminderDetailUiEvent.OnShowDeleteDialogClick -> {
                updateState { copy(showDeleteDialog = true) }
            }
            ReminderDetailUiEvent.OnDismissDeleteDialogClick -> {
                updateState { copy(showDeleteDialog = false) }
            }
            ReminderDetailUiEvent.OnConfirmDeleteClick -> {
                updateState { copy(showDeleteDialog = false) }
                viewModelScope.launch {
                    reminderRepository.deleteReminder(reminderId)
                    sendEffect(ReminderDetailUiSideEffect.NavigateBack)
                }
            }
            ReminderDetailUiEvent.OnEditReminderClick -> {
                sendEffect(ReminderDetailUiSideEffect.NavigateToEdit(reminderId))
            }
            ReminderDetailUiEvent.OnRestoreReminderClick -> {
                val hasUnpackedItem = currentState.localItems.any { !it.isPacked }
                if (!hasUnpackedItem) {
                    updateState { copy(restoreWarning = "Uncheck at least 1 item to restore reminder") }
                } else {
                    viewModelScope.launch {
                        reminderRepository.restoreReminder(reminderId)
                        updateState { copy(isCompleted = false, restoreWarning = null) }
                        sendEffect(ReminderDetailUiSideEffect.ShowSnackbar("Reminder restored as active"))
                    }
                }
            }
            ReminderDetailUiEvent.OnBackClick -> {
                sendEffect(ReminderDetailUiSideEffect.NavigateBack)
            }
        }
    }
}
