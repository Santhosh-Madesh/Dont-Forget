# UI State — Chunk 01: Reminders & Home Screen (`chunk-01-home-and-reminders`)

## 1. Home UI State
```kotlin
data class HomeUiState(
    val isLoading: Boolean = true,
    val reminders: List<ReminderWithItems> = emptyList(),
    val isNotificationPermissionGranted: Boolean = true
) : UiState
```

## 2. Reminder Detail UI State
```kotlin
data class ReminderDetailUiState(
    val isLoading: Boolean = true,
    val reminderWithItems: ReminderWithItems? = null,
    val localItems: List<ChecklistItemEntity> = emptyList(),
    val showDeleteDialog: Boolean = false,
    val showMarkCompleteCard: Boolean = false,
    val isCompleted: Boolean = false,
    val restoreWarning: String? = null
) : UiState {
    val isAllPacked: Boolean
        get() = localItems.isNotEmpty() && localItems.all { it.isPacked }
}
```
