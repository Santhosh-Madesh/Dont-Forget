# UI State — Chunk 05: Notification Display Feature (`chunk-05-notification-display`)

## 1. Notification Item Data Class
```kotlin
data class NotificationItem(
    val reminderId: Long,
    val title: String,
    val content: String,
    val triggerEpochMillis: Long,
    val departureEpochMillis: Long,
    val isTriggered: Boolean
)
```

## 2. Notification UI State Definition
```kotlin
data class NotificationUiState(
    val isLoading: Boolean = true,
    val upcomingNotifications: List<NotificationItem> = emptyList(),
    val previousNotifications: List<NotificationItem> = emptyList()
) : UiState {
    val upcomingCount: Int get() = upcomingNotifications.size
    val previousCount: Int get() = previousNotifications.size
    val isEmpty: Boolean get() = !isLoading && upcomingNotifications.isEmpty() && previousNotifications.isEmpty()
}
```
