# Events — Chunk 01: Reminders & Home Screen (`chunk-01-home-and-reminders`)

## 1. Home UI Events
```kotlin
sealed interface HomeUiEvent : UiEvent {
    data class OnReminderClick(val reminderId: Long) : HomeUiEvent
    object OnCreateReminderClick : HomeUiEvent
    object OnTemplatesClick : HomeUiEvent
    object OnOpenNotificationSettingsClick : HomeUiEvent
}
```

## 2. Reminder Detail UI Events
```kotlin
sealed interface ReminderDetailUiEvent : UiEvent {
    data class OnToggleItemPacked(val itemId: Long, val isPacked: Boolean) : ReminderDetailUiEvent
    object OnPackAllClick : ReminderDetailUiEvent
    object OnUpdateClick : ReminderDetailUiEvent
    object OnMarkCompleteClick : ReminderDetailUiEvent
    object OnConfirmDeleteClick : ReminderDetailUiEvent
    object OnShowDeleteDialogClick : ReminderDetailUiEvent
    object OnDismissDeleteDialogClick : ReminderDetailUiEvent
    object OnEditReminderClick : ReminderDetailUiEvent
    object OnRestoreReminderClick : ReminderDetailUiEvent
    object OnBackClick : ReminderDetailUiEvent
}
```
