# ViewModel — Chunk 01: Home Feature (`chunk-01-home-and-reminders`)

## HomeViewModel Specification

### Class Signature & Inheritance
```kotlin
class HomeViewModel(
    private val reminderRepository: ReminderRepository
) : BaseViewModel<HomeUiState, HomeUiEvent, HomeUiSideEffect>(HomeUiState())
```

### Initialization & Data Collection
Upon `init`, `HomeViewModel` launches two coroutines in `viewModelScope`:
1. **Active Reminders Observation**:
   - Collects `reminderRepository.getActiveReminders()`.
   - Updates state: `copy(isLoading = false, activeReminders = reminders)`.
2. **Completed Reminders Observation**:
   - Collects `reminderRepository.getCompletedReminders()`.
   - Updates state: `copy(completedReminders = completed)`.

### Event Processing (`onEvent`)
```kotlin
override fun onEvent(event: HomeUiEvent) {
    when (event) {
        is HomeUiEvent.OnFilterTabSelected -> {
            updateState { copy(selectedFilter = event.filterTab) }
        }
        is HomeUiEvent.OnReminderClick -> {
            sendEffect(HomeUiSideEffect.NavigateToDetail(event.reminderId))
        }
        HomeUiEvent.OnCreateReminderClick -> {
            sendEffect(HomeUiSideEffect.NavigateToCreateReminder)
        }
        HomeUiEvent.OnOpenNotificationSettingsClick -> {
            sendEffect(HomeUiSideEffect.OpenNotificationSettings)
        }
        HomeUiEvent.OnNotificationIconClick -> {
            // Mock notification action (no navigation)
        }
        is HomeUiEvent.OnNotificationPermissionStatusChanged -> {
            updateState { copy(isNotificationPermissionGranted = event.isGranted) }
        }
    }
}
```
