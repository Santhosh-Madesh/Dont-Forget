package com.example.dontforget.feature.reminderdetail

import com.example.dontforget.core.mvi.UiEvent
import com.example.dontforget.core.mvi.UiSideEffect
import com.example.dontforget.core.mvi.UiState
import com.example.dontforget.data.local.entity.ChecklistItemEntity
import com.example.dontforget.data.local.model.ReminderWithItems

data class ReminderDetailUiState(
    val isLoading: Boolean = true,
    val reminderWithItems: ReminderWithItems? = null,
    val localItems: List<ChecklistItemEntity> = emptyList(),
    val showDeleteDialog: Boolean = false,
    val isCompleted: Boolean = false,
    val restoreWarning: String? = null
) : UiState {
    val isAllPacked: Boolean
        get() = localItems.isNotEmpty() && localItems.all { it.isPacked }
}

sealed interface ReminderDetailUiEvent : UiEvent {
    data class OnToggleItemPacked(val itemId: Long, val isPacked: Boolean) : ReminderDetailUiEvent
    object OnPackAllClick : ReminderDetailUiEvent
    object OnUnpackAllClick : ReminderDetailUiEvent
    object OnUpdateClick : ReminderDetailUiEvent
    object OnMarkCompleteClick : ReminderDetailUiEvent
    object OnConfirmDeleteClick : ReminderDetailUiEvent
    object OnShowDeleteDialogClick : ReminderDetailUiEvent
    object OnDismissDeleteDialogClick : ReminderDetailUiEvent
    object OnEditReminderClick : ReminderDetailUiEvent
    object OnRestoreReminderClick : ReminderDetailUiEvent
    object OnBackClick : ReminderDetailUiEvent
}

sealed interface ReminderDetailUiSideEffect : UiSideEffect {
    data class NavigateToEdit(val reminderId: Long) : ReminderDetailUiSideEffect
    object NavigateBack : ReminderDetailUiSideEffect
    data class ShowSnackbar(val message: String) : ReminderDetailUiSideEffect
}
