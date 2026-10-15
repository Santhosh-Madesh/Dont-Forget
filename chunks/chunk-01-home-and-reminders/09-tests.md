# Unit Tests — Chunk 01: Home Feature (`chunk-01-home-and-reminders`)

## 1. Planned Unit Tests (`HomeViewModelTest`)

### Test Scenarios
1. **Initial Data Observation**:
   - `initial state collects active and completed reminders from repository`
2. **Filter Tab Selection**:
   - `selecting filter tab updates selectedFilter in HomeUiState`
3. **Card Navigation Effect**:
   - `onReminderClick emits NavigateToDetail side effect with reminderId`
4. **FAB Navigation Effect**:
   - `onCreateReminderClick emits NavigateToCreateReminder side effect`
5. **Notification Permission Action**:
   - `onOpenNotificationSettingsClick emits OpenNotificationSettings side effect`
6. **Permission State Update**:
   - `onNotificationPermissionStatusChanged updates isNotificationPermissionGranted in HomeUiState`
7. **Active Categorization Logic**:
   - `todayReminders, tomorrowReminders, and upcomingReminders correctly partition active reminders by departure date`

## 2. Test File Location
- [`app/src/test/java/com/example/dontforget/feature/home/HomeViewModelTest.kt`](file:///D:/DontForget/app/src/test/java/com/example/dontforget/feature/home/HomeViewModelTest.kt)
