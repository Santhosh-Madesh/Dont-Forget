# Navigation — Chunk 05: Notification Display Feature (`chunk-05-notification-display`)

## Route & Navigation Graph Wiring

### 1. Route Definition
- Route object: `Route.Notification`
- Route string: `"notification"`

### 2. Destination Composables & Callbacks in `MainActivity.kt`
```kotlin
composable(Route.Notification.route) {
    val notificationViewModel: NotificationViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return NotificationViewModel(reminderRepo) as T
            }
        }
    )
    NotificationScreen(
        viewModel = notificationViewModel,
        onNavigateBack = {
            navController.popBackStack()
        }
    )
}
```
