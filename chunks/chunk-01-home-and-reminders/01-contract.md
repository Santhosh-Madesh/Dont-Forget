# Feature Contract — Chunk 01: Home Feature (`chunk-01-home-and-reminders`)

## 1. Goal
Provide the Home Screen displaying today's date header, "Reminders" section header, notification warning card (only when permissions are missing), Active vs Completed filter toggle, active reminders categorized into "Today", "Tomorrow", and "Upcoming", completed reminders list, detailed reminder card tags (notification lead time, packing status, urgency tag), and FAB navigation to Create Reminder.

## 2. Scope (Home Feature Focus)

### In Scope
- **Top App Bar**:
  - Title: "DONT FORGET"
  - Action: Mock notification icon button (no navigation action for now).
- **Content Header Section**:
  - Today's date header (e.g. "Thursday, Oct 15").
  - "Reminders" section title.
- **Notification Warning Card**:
  - Rendered ONLY if notification permissions are NOT granted.
  - Hidden completely when permissions are granted.
  - Provides CTA button to open system settings.
- **Active / Completed Filter Toggle**:
  - Toggle buttons/card switch between "Active" and "Completed" views.
- **Active Reminders Categorized List**:
  - Reminders grouped into 3 date categories based on departure timestamp:
    - **Today**: Departure date matches today's date.
    - **Tomorrow**: Departure date matches tomorrow's date.
    - **Upcoming**: Departure date is beyond tomorrow's date.
- **Reminder Card Component Tags (`ReminderCard`)**:
  - Context Icon + Context Title + Departure Date & Time.
  - **Notification Lead Tag**: Notification bell icon + lead time text (e.g., "30 min before").
  - **Packing Status Tag**: Status badge such as "Fully packed" (100% packed), "Not started" (0% packed), or "X/Y Packed".
  - **Urgency Tag**: If departure is within 1 hour (<= 60 minutes), display an urgency tag (e.g., "Leaves in 45 min").
- **Completed Reminders List**:
  - Displays completed reminders when "Completed" tab is active.
- **Empty States**:
  - Displays contextual empty state when no active or completed reminders exist in the selected filter view.
- **Floating Action Button (FAB)**:
  - "+ New Reminder" button emitting navigation effect to `Route.CreateReminder`.
- **Card Interaction**:
  - Tapping a reminder card emits navigation effect to `Route.ReminderDetail(reminderId)`.

### Out of Scope
- Reminder Overview / Detail screen checklist packing logic (separate feature scope).
- Reminder Creation / Edit form inputs (Chunk 2).
- Template management screens (Chunk 3).
- Real system notification receivers and trigger handling (Chunk 4).

## 3. Data & Dependencies
- **Consumes**:
  - `ReminderRepository`:
    - `getActiveReminders(): Flow<List<ReminderWithItems>>`
    - `getCompletedReminders(): Flow<List<ReminderWithItems>>` (or unified stream)
    - `deleteInactiveRemindersOlderThan(cutoffEpochMillis: Long)`

## 4. Exposed Surface
- `HomeScreen` composable
- Navigation events to `Route.CreateReminder`, `Route.ReminderDetail(reminderId)`, and System Settings.

## 5. Acceptance Criteria (Home Feature)
- **AC-HOME-1**: `HomeScreen` top bar renders "DONT FORGET" title and mock notification icon button.
- **AC-HOME-2**: Content header displays today's formatted date (Day, Month Date) followed by the "Reminders" section header.
- **AC-HOME-3**: Notification Warning Card is visible ONLY when notification permissions are NOT granted, and hidden when granted.
- **AC-HOME-4**: Filter toggle allows switching between "Active" and "Completed" views.
- **AC-HOME-5**: Active reminders are categorized under "Today", "Tomorrow", and "Upcoming" headers based on departure dates.
- **AC-HOME-6**: `ReminderCard` displays notification lead time tag (with bell icon), packing status ("Fully packed", "Not started", or "X/Y Packed"), and urgency tag ("Leaves in X min") if departure is within 1 hour.
- **AC-HOME-7**: Selecting "Completed" tab displays the completed reminders list.
- **AC-HOME-8**: Displays contextual empty state when no reminders exist in the selected filter view.
- **AC-HOME-9**: Tapping FAB "+ New Reminder" navigates to `Route.CreateReminder`.
- **AC-HOME-10**: Tapping a reminder card navigates to `Route.ReminderDetail(reminderId)`.
