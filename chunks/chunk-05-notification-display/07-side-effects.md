# Side Effects — Chunk 05: Notification Display Feature (`chunk-05-notification-display`)

## Notification Side Effects Definition

```kotlin
sealed interface NotificationUiSideEffect : UiSideEffect {
    /** Navigates back to the previous screen (Home) */
    object NavigateBack : NotificationUiSideEffect
}
```
