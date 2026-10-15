# Side Effects — Chunk 01: Home Feature (`chunk-01-home-and-reminders`)

## Home Side Effects Definition

```kotlin
sealed interface HomeUiSideEffect : UiSideEffect {
    /** Navigates to the Reminder Overview / Detail Screen for a specific reminder */
    data class NavigateToDetail(val reminderId: Long) : HomeUiSideEffect

    /** Navigates to the Create Reminder Screen */
    object NavigateToCreateReminder : HomeUiSideEffect

    /** Opens the Android System Application Settings for notification permissions */
    object OpenNotificationSettings : HomeUiSideEffect
}
```
