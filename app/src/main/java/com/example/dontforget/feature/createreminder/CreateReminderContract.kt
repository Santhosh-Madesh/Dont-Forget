package com.example.dontforget.feature.createreminder

import com.example.dontforget.core.mvi.UiEvent
import com.example.dontforget.core.mvi.UiSideEffect
import com.example.dontforget.core.mvi.UiState
import com.example.dontforget.data.local.model.TemplateWithContextAndItems

enum class CreateReminderStep {
    SELECT_TEMPLATE,
    CONTEXT,
    CHECKLIST,
    DATE_TIME_NOTIFICATION,
    PREVIEW
}

data class CreateReminderUiState(
    val currentStep: CreateReminderStep = CreateReminderStep.SELECT_TEMPLATE,
    val availableTemplates: List<TemplateWithContextAndItems> = emptyList(),
    val selectedTemplateId: Long? = null,
    val contextTitle: String = "",
    val contextIconName: String = "event",
    val checklistItems: List<String> = emptyList(),
    val departureEpochMillis: Long = System.currentTimeMillis() + 3600000L,
    val notificationLeadMinutes: Int = 30,
    val isCustomLeadTime: Boolean = false,
    val customLeadMinutesInput: String = "30",
    val saveAsTemplate: Boolean = false,
    val templateTitleInput: String = "",
    val validationError: String? = null,
    val showDiscardDialog: Boolean = false,
    val isLoading: Boolean = false
) : UiState

sealed interface CreateReminderUiEvent : UiEvent {
    data class SelectStep(val step: CreateReminderStep) : CreateReminderUiEvent
    data object NextStep : CreateReminderUiEvent
    data object PreviousStep : CreateReminderUiEvent
    data class SelectTemplate(val templateId: Long) : CreateReminderUiEvent
    data object SkipTemplate : CreateReminderUiEvent
    data class UpdateContextTitle(val title: String) : CreateReminderUiEvent
    data class SelectContextIcon(val iconName: String) : CreateReminderUiEvent
    data class AddChecklistItem(val itemText: String) : CreateReminderUiEvent
    data class EditChecklistItem(val index: Int, val newText: String) : CreateReminderUiEvent
    data class RemoveChecklistItem(val index: Int) : CreateReminderUiEvent
    data class ReorderChecklistItem(val fromIndex: Int, val toIndex: Int) : CreateReminderUiEvent
    data class SelectDepartureDateTime(val epochMillis: Long) : CreateReminderUiEvent
    data class SelectNotificationLeadMinutes(val minutes: Int) : CreateReminderUiEvent
    data class UpdateCustomLeadMinutes(val input: String) : CreateReminderUiEvent
    data class ToggleSaveAsTemplate(val enabled: Boolean) : CreateReminderUiEvent
    data class UpdateTemplateTitle(val title: String) : CreateReminderUiEvent
    data object SaveReminder : CreateReminderUiEvent
    data object RequestDiscard : CreateReminderUiEvent
    data object ConfirmDiscard : CreateReminderUiEvent
    data object DismissDiscardDialog : CreateReminderUiEvent
}

sealed interface CreateReminderUiSideEffect : UiSideEffect {
    data object NavigateHome : CreateReminderUiSideEffect
    data class NavigateToReminderDetail(val reminderId: Long) : CreateReminderUiSideEffect
    data class ShowToast(val message: String) : CreateReminderUiSideEffect
}
