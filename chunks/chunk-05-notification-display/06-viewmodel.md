# ViewModel — Chunk 05: Notification Display Feature (`chunk-05-notification-display`)

## NotificationViewModel Specification

### Class Signature & Inheritance
```kotlin
class NotificationViewModel(
    private val reminderRepository: ReminderRepository
) : BaseViewModel<NotificationUiState, NotificationUiEvent, NotificationUiSideEffect>(
    initialState = NotificationUiState()
)
```

### Data Observation
In `init`, combines active and completed reminders from `ReminderRepository`, maps them to `NotificationItem` objects, splits them into `upcomingNotifications` and `previousNotifications`, and updates `NotificationUiState`.

### Event Processing (`onEvent`)
```kotlin
override fun onEvent(event: NotificationUiEvent) {
    when (event) {
        NotificationUiEvent.OnBackClick -> {
            sendEffect(NotificationUiSideEffect.NavigateBack)
        }
    }
}
```
