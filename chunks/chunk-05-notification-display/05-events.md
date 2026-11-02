# Events — Chunk 05: Notification Display Feature (`chunk-05-notification-display`)

## Notification UI Events
```kotlin
sealed interface NotificationUiEvent : UiEvent {
    /** Tapping the top app bar back navigation button */
    object OnBackClick : NotificationUiEvent
}
```
