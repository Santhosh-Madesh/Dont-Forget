package com.example.dontforget.feature.editreminder

import androidx.lifecycle.viewModelScope
import com.example.dontforget.core.mvi.BaseViewModel
import com.example.dontforget.data.local.entity.ChecklistItemEntity
import com.example.dontforget.data.repository.ReminderRepository
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class EditReminderViewModel(
    private val reminderRepository: ReminderRepository
) : BaseViewModel<EditReminderUiState, EditReminderUiEvent, EditReminderUiSideEffect>(
    initialState = EditReminderUiState()
) {

    override fun onEvent(event: EditReminderUiEvent) {
        when (event) {
            is EditReminderUiEvent.LoadReminder -> {
                loadReminder(event.reminderId)
            }
            is EditReminderUiEvent.UpdateContextTitle -> {
                updateState { copy(contextTitle = event.title, isModified = true, validationError = null) }
            }
            is EditReminderUiEvent.SelectContextIcon -> {
                updateState { copy(contextIconName = event.iconName, isModified = true) }
            }
            is EditReminderUiEvent.AddChecklistItem -> {
                val trimmed = event.text.trim()
                if (trimmed.isNotEmpty()) {
                    updateState {
                        copy(
                            checklistItems = checklistItems + (0L to trimmed),
                            isModified = true,
                            validationError = null
                        )
                    }
                }
            }
            is EditReminderUiEvent.EditChecklistItem -> {
                val list = currentState.checklistItems.toMutableList()
                if (event.index in list.indices) {
                    val currentPair = list[event.index]
                    list[event.index] = currentPair.first to event.text
                    updateState { copy(checklistItems = list, isModified = true) }
                }
            }
            is EditReminderUiEvent.RemoveChecklistItem -> {
                val list = currentState.checklistItems.toMutableList()
                if (event.index in list.indices) {
                    list.removeAt(event.index)
                    updateState { copy(checklistItems = list, isModified = true) }
                }
            }
            is EditReminderUiEvent.ReorderChecklistItem -> {
                val list = currentState.checklistItems.toMutableList()
                if (event.fromIndex in list.indices && event.toIndex in list.indices) {
                    val item = list.removeAt(event.fromIndex)
                    list.add(event.toIndex, item)
                    updateState { copy(checklistItems = list, isModified = true) }
                }
            }
            is EditReminderUiEvent.SelectDepartureDateTime -> {
                updateState { copy(departureEpochMillis = event.epochMillis, isModified = true, validationError = null) }
            }
            is EditReminderUiEvent.SelectNotificationLeadMinutes -> {
                updateState {
                    copy(
                        notificationLeadMinutes = event.minutes,
                        isCustomLeadTime = false,
                        isModified = true,
                        validationError = null
                    )
                }
            }
            is EditReminderUiEvent.UpdateCustomLeadMinutes -> {
                val mins = event.input.toIntOrNull() ?: 30
                updateState {
                    copy(
                        customLeadMinutesInput = event.input,
                        notificationLeadMinutes = mins,
                        isCustomLeadTime = true,
                        isModified = true,
                        validationError = null
                    )
                }
            }
            EditReminderUiEvent.SaveChanges -> {
                saveChangesInternal()
            }
            EditReminderUiEvent.RequestDiscard -> {
                if (currentState.isModified) {
                    updateState { copy(showDiscardDialog = true) }
                } else {
                    sendEffect(EditReminderUiSideEffect.NavigateBack)
                }
            }
            EditReminderUiEvent.ConfirmDiscard -> {
                updateState { copy(showDiscardDialog = false) }
                sendEffect(EditReminderUiSideEffect.NavigateBack)
            }
            EditReminderUiEvent.DismissDiscardDialog -> {
                updateState { copy(showDiscardDialog = false) }
            }
        }
    }

    private fun loadReminder(reminderId: Long) {
        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            val reminderWithItems = reminderRepository.getReminderById(reminderId).firstOrNull()
            if (reminderWithItems != null) {
                val rem = reminderWithItems.reminder
                val sortedItems = reminderWithItems.items.sortedBy { it.displayOrder }
                val presets = listOf(15, 30, 45, 60)
                val isCustom = rem.notificationLeadMinutes !in presets
                updateState {
                    copy(
                        reminderId = rem.id,
                        contextTitle = rem.contextTitle,
                        contextIconName = rem.contextIconName,
                        checklistItems = sortedItems.map { it.id to it.text },
                        departureEpochMillis = rem.departureEpochMillis,
                        notificationLeadMinutes = rem.notificationLeadMinutes,
                        isCustomLeadTime = isCustom,
                        customLeadMinutesInput = rem.notificationLeadMinutes.toString(),
                        isModified = false,
                        isLoading = false
                    )
                }
            } else {
                updateState { copy(isLoading = false, validationError = "Reminder not found") }
            }
        }
    }

    private fun saveChangesInternal() {
        val state = currentState

        if (state.contextTitle.isBlank()) {
            updateState { copy(validationError = "Context title cannot be empty") }
            return
        }
        if (state.checklistItems.isEmpty()) {
            updateState { copy(validationError = "Please add at least one checklist item") }
            return
        }
        if (state.departureEpochMillis <= System.currentTimeMillis()) {
            updateState { copy(validationError = "Departure time cannot be in the past") }
            return
        }

        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            val existing = reminderRepository.getReminderEntityById(state.reminderId)
            if (existing != null) {
                val updatedReminder = existing.copy(
                    contextTitle = state.contextTitle,
                    contextIconName = state.contextIconName,
                    departureEpochMillis = state.departureEpochMillis,
                    notificationLeadMinutes = state.notificationLeadMinutes
                )

                val updatedItems = state.checklistItems.mapIndexed { index, pair ->
                    ChecklistItemEntity(
                        id = pair.first,
                        parentId = state.reminderId,
                        parentType = "REMINDER",
                        text = pair.second,
                        isPacked = false,
                        displayOrder = index
                    )
                }

                reminderRepository.updateReminder(updatedReminder, updatedItems)
                updateState { copy(isLoading = false) }
                sendEffect(EditReminderUiSideEffect.NavigateBack)
            } else {
                updateState { copy(isLoading = false, validationError = "Failed to update reminder") }
            }
        }
    }
}
