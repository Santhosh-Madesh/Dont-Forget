# Behaviour — Chunk 05: Notification Display Feature (`chunk-05-notification-display`)

## 1. Data Processing & Trigger Calculation
- **Repository Observation**:
  - Combines `reminderRepository.getActiveReminders()` and `reminderRepository.getCompletedReminders()`.
- **Trigger Timestamp Calculation**:
  - For each reminder: `triggerEpochMillis = departureEpochMillis - (notificationLeadMinutes * 60 * 1000)`.
  - Determines status: `isTriggered = triggerEpochMillis <= System.currentTimeMillis()`.

## 2. Categorization & Sorting
- **UPCOMING Notifications**:
  - Filter: `!isTriggered` (`triggerEpochMillis > currentTime`).
  - Order: Sorted ascending by `triggerEpochMillis` (closest upcoming item first).
- **PREVIOUS Notifications**:
  - Filter: `isTriggered` (`triggerEpochMillis <= currentTime`).
  - Order: Sorted descending by `triggerEpochMillis` (most recent past item first).

## 3. String Content & Localization
- Notification content string formatted as `"Reminder: $title"` using string resources from `strings_notification_display.xml`.
- UI strings consumed via `stringResource(R.string...)` or context resource strings.

## 4. Navigation Behaviour
- Tapping Top Bar Back Icon -> emits `NotificationUiEvent.OnBackClick` -> sends `NotificationUiSideEffect.NavigateBack` -> pops backstack to Home Screen.
