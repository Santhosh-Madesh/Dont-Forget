# Side Effects Specification — Chunk 02: Create & Edit Reminder Flow

```kotlin
sealed interface CreateReminderUiSideEffect : UiSideEffect {
    data object NavigateHome : CreateReminderUiSideEffect
    data class NavigateToReminderDetail(val reminderId: Long) : CreateReminderUiSideEffect
    data class ShowToast(val message: String) : CreateReminderUiSideEffect
}

sealed interface EditReminderUiSideEffect : UiSideEffect {
    data object NavigateBack : EditReminderUiSideEffect
    data class NavigateToReminderDetail(val reminderId: Long) : EditReminderUiSideEffect
    data class ShowToast(val message: String) : EditReminderUiSideEffect
}
```
