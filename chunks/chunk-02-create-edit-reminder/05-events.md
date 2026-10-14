# UI Events Specification — Chunk 02: Create & Edit Reminder Flow

```kotlin
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
```
