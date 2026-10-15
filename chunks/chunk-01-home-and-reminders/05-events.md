# Events — Chunk 01: Home Feature (`chunk-01-home-and-reminders`)

## Home UI Events
```kotlin
sealed interface HomeUiEvent : UiEvent {
    /** Tapping a reminder card to view its overview/details */
    data class OnReminderClick(val reminderId: Long) : HomeUiEvent

    /** Switching between "Active" and "Completed" filter tabs */
    data class OnFilterTabSelected(val filterTab: HomeFilterTab) : HomeUiEvent

    /** Tapping the Floating Action Button ("+ New Reminder") */
    object OnCreateReminderClick : HomeUiEvent

    /** Tapping "Enable in Settings" on the notification warning card */
    object OnOpenNotificationSettingsClick : HomeUiEvent

    /** Tapping the mock notification icon in the top app bar */
    object OnNotificationIconClick : HomeUiEvent

    /** Updating runtime notification permission state */
    data class OnNotificationPermissionStatusChanged(val isGranted: Boolean) : HomeUiEvent
}
```
