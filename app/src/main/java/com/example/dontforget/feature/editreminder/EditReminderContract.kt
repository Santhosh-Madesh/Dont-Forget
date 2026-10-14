package com.example.dontforget.feature.editreminder

import com.example.dontforget.core.mvi.UiEvent
import com.example.dontforget.core.mvi.UiSideEffect
import com.example.dontforget.core.mvi.UiState

data class EditReminderUiState(
    val reminderId: Long = 0L,
    val contextTitle: String = "",
    val contextIconName: String = "event",
    val checklistItems: List<Pair<Long, String>> = emptyList(),
    val departureEpochMillis: Long = System.currentTimeMillis() + 3600000L,
    val notificationLeadMinutes: Int = 30,
    val isCustomLeadTime: Boolean = false,
    val customLeadMinutesInput: String = "30",
    val validationError: String? = null,
    val showDiscardDialog: Boolean = false,
    val isModified: Boolean = false,
    val isLoading: Boolean = true
) : UiState

sealed interface EditReminderUiEvent : UiEvent {
    data class LoadReminder(val reminderId: Long) : EditReminderUiEvent
    data class UpdateContextTitle(val title: String) : EditReminderUiEvent
    data class SelectContextIcon(val iconName: String) : EditReminderUiEvent
    data class AddChecklistItem(val text: String) : EditReminderUiEvent
    data class EditChecklistItem(val index: Int, val text: String) : EditReminderUiEvent
    data class RemoveChecklistItem(val index: Int) : EditReminderUiEvent
    data class ReorderChecklistItem(val fromIndex: Int, val toIndex: Int) : EditReminderUiEvent
    data class SelectDepartureDateTime(val epochMillis: Long) : EditReminderUiEvent
    data class SelectNotificationLeadMinutes(val minutes: Int) : EditReminderUiEvent
    data class UpdateCustomLeadMinutes(val input: String) : EditReminderUiEvent
    data object SaveChanges : EditReminderUiEvent
    data object RequestDiscard : EditReminderUiEvent
    data object ConfirmDiscard : EditReminderUiEvent
    data object DismissDiscardDialog : EditReminderUiEvent
}

sealed interface EditReminderUiSideEffect : UiSideEffect {
    data object NavigateBack : EditReminderUiSideEffect
    data class NavigateToReminderDetail(val reminderId: Long) : EditReminderUiSideEffect
    data class ShowToast(val message: String) : EditReminderUiSideEffect
}
