# Side Effects — Chunk 01: Reminders & Home Screen (`chunk-01-home-and-reminders`)

## 1. Home Side Effects
```kotlin
sealed interface HomeUiSideEffect : UiSideEffect {
    data class NavigateToDetail(val reminderId: Long) : HomeUiSideEffect
    object NavigateToCreateReminder : HomeUiSideEffect
    object NavigateToTemplates : HomeUiSideEffect
    object OpenNotificationSettings : HomeUiSideEffect
}
```

## 2. Reminder Detail Side Effects
```kotlin
sealed interface ReminderDetailUiSideEffect : UiSideEffect {
    data class NavigateToEdit(val reminderId: Long) : ReminderDetailUiSideEffect
    object NavigateBack : ReminderDetailUiSideEffect
    data class ShowSnackbar(val message: String) : ReminderDetailUiSideEffect
}
```
