package com.example.dontforget.feature.reminderdetail

import android.util.Log
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
        Log.d("viewModel", "viewModel is initiaizling & calling observeReminder")
        observeReminder()
        Log.d("viewModel", "Called observeReminder")
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
            }
            ReminderDetailUiEvent.OnPackAllClick -> {
                val updated = currentState.localItems.map { it.copy(isPacked = true) }
                updateState { copy(localItems = updated) }
            }
            ReminderDetailUiEvent.OnUnpackAllClick -> {
                val updated = currentState.localItems.map { it.copy(isPacked = false) }
                updateState { copy(localItems = updated) }
            }
            ReminderDetailUiEvent.OnUpdateClick -> {
                viewModelScope.launch {
                    reminderRepository.updateChecklistItems(currentState.localItems)
                    sendEffect(ReminderDetailUiSideEffect.ShowSnackbar("Checklist updated"))
                }
            }
            ReminderDetailUiEvent.OnMarkCompleteClick -> {
                viewModelScope.launch {
                    reminderRepository.updateChecklistItems(currentState.localItems)
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
                        reminderRepository.updateChecklistItems(currentState.localItems)
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
