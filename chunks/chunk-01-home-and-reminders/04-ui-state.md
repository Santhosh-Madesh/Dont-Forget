# UI State — Chunk 01: Home Feature (`chunk-01-home-and-reminders`)

## 1. Filter Tab Enum
```kotlin
enum class HomeFilterTab {
    ACTIVE,
    COMPLETED
}
```

## 2. Home UI State Definition
```kotlin
data class HomeUiState(
    val isLoading: Boolean = true,
    val selectedFilter: HomeFilterTab = HomeFilterTab.ACTIVE,
    val activeReminders: List<ReminderWithItems> = emptyList(),
    val completedReminders: List<ReminderWithItems> = emptyList(),
    val isNotificationPermissionGranted: Boolean = true
) : UiState {

    /**
     * Active reminders departing prior to today's start of day
     */
    val overdueReminders: List<ReminderWithItems>
        get() = activeReminders.filter { isOverdue(it.reminder.departureEpochMillis) }

    /**
     * Active reminders departing today
     */
    val todayReminders: List<ReminderWithItems>
        get() = activeReminders.filter { isToday(it.reminder.departureEpochMillis) }

    /**
     * Active reminders departing tomorrow
     */
    val tomorrowReminders: List<ReminderWithItems>
        get() = activeReminders.filter { isTomorrow(it.reminder.departureEpochMillis) }

    /**
     * Active reminders departing after tomorrow
     */
    val upcomingReminders: List<ReminderWithItems>
        get() = activeReminders.filter { isUpcoming(it.reminder.departureEpochMillis) }

    /**
     * Whether the current filter view has no items to display
     */
    val isCurrentViewEmpty: Boolean
        get() = when (selectedFilter) {
            HomeFilterTab.ACTIVE -> activeReminders.isEmpty()
            HomeFilterTab.COMPLETED -> completedReminders.isEmpty()
        }
}
```
