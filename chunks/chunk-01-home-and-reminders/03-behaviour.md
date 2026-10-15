# Behaviour — Chunk 01: Home Feature (`chunk-01-home-and-reminders`)

## 1. Data Collection & Filtering
- **Repository Subscription**:
  - `HomeViewModel` observes `ReminderRepository.getActiveReminders()` and `ReminderRepository.getCompletedReminders()`.
  - When database changes occur in Room, state updates reactively.
- **Categorization Logic (Active Reminders)**:
  - Active reminders are partitioned into three categories based on departure timestamp:
    - **Today**: Departure date falls on today's calendar day (`isToday(departureEpochMillis)`).
    - **Tomorrow**: Departure date falls on tomorrow's calendar day (`isTomorrow(departureEpochMillis)`).
    - **Upcoming**: Departure date is after tomorrow (`isUpcoming(departureEpochMillis)`).

## 2. Filter Tab Toggle Behaviour
- User clicking "Active" or "Completed" toggle emits `OnFilterTabSelected(HomeFilterTab)`.
- Updates `HomeUiState.selectedFilter`.
- If `Active` is selected, renders the 3 categorized active sections ("Today", "Tomorrow", "Upcoming").
- If `Completed` is selected, renders the list of completed reminders.

## 3. Notification Permission Warning Card Behaviour
- Evaluates `isNotificationPermissionGranted` from system/state.
- **If `false`**: Displays Notification Warning Card (hugging container width). Tapping "Enable in Settings" emits `OnOpenNotificationSettingsClick` -> sends `OpenNotificationSettings` side effect.
- **If `true`**: Card is hidden completely.

## 4. Reminder Card Tag Logic
- **Notification Lead Time**: Displays `notificationLeadMinutes` as `"X min before"` (with bell icon).
- **Packing Status**:
  - If `packedCount == totalCount && totalCount > 0`: Displays `"Fully packed"`.
  - Else if `packedCount == 0`: Displays `"Not started"`.
  - Else: Displays `"X/Y Packed"`.
- **Urgency Tag**:
  - Calculates time remaining until departure: `diffMinutes = (departureEpochMillis - currentTimeMillis) / 60000`.
  - If `diffMinutes in 1..60`: Renders urgency badge `"Leaves in X min"`.

## 5. Navigation & Action Events
- Tapping a reminder card -> emits `OnReminderClick(reminderId)` -> sends `NavigateToDetail(reminderId)` side effect.
- Tapping FAB "+ New Reminder" -> emits `OnCreateReminderClick` -> sends `NavigateToCreateReminder` side effect.
- Tapping Top Bar Notification Icon -> emits `OnNotificationIconClick` -> mock handling (no navigation action for now).
