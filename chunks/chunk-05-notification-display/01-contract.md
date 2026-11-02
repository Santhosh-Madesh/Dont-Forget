# Feature Contract — Chunk 05: Notification Display Feature (`chunk-05-notification-display`)

## 1. Goal
Provide the Notification Display Screen showing all notifications categorized by "UPCOMING" (yet to be triggered) and "PREVIOUS" (already triggered) sections with inline category badge counts, exact notification builder content text, empty state view, and back navigation.

## 2. Scope (Notification Display Feature Focus)

### In Scope
- **Top App Bar**:
  - Title: `"Notifications"`
  - Navigation Icon: Back arrow icon returning to Home or previous screen.
  - Action item: Removed (no circular avatar).
- **UPCOMING Section**:
  - Header: `"UPCOMING"` in primary accent color with inline badge count (e.g. `UPCOMING 2`).
  - Items list: Title, formatted relative departure date & time, content subtitle (`"Reminder: $title"`), item dividers.
- **PREVIOUS Section**:
  - Header: `"PREVIOUS"` in muted color with inline badge count (e.g. `PREVIOUS 2`).
  - Items list: Title, formatted relative departure date & time, content subtitle (`"Reminder: $title"`), item dividers.
- **Empty State**:
  - Rendered when both UPCOMING and PREVIOUS categories have 0 items.
  - Displays `NotificationsNone` icon, title `"No Notifications"`, and subtitle description.
- **Footer**:
  - Centered text `"DONT FORGET · LOCAL HISTORY"`.
- **Card Interaction**:
  - Cards are display-only (non-clickable).

### Out of Scope
- Direct alarm scheduling and broadcast receivers (Chunk 4).
- Reminder creation and editing (Chunk 2).

## 3. Data & Dependencies
- **Consumes**:
  - `ReminderRepository`: `getActiveReminders()`, `getCompletedReminders()`

## 4. Exposed Surface
- `NotificationScreen` composable
- Navigation back event.

## 5. Acceptance Criteria
- **AC-NOTIF-1**: Top app bar renders "Notifications" title and back button without action avatar.
- **AC-NOTIF-2**: Categorizes items into "UPCOMING" and "PREVIOUS" based on trigger timestamp relative to system current time.
- **AC-NOTIF-3**: Headers display badge counts inline directly beside section titles (`UPCOMING 2`, `PREVIOUS 2`).
- **AC-NOTIF-4**: Subtitle content matches the string passed to the system notification builder.
- **AC-NOTIF-5**: Render centered empty state illustration when no notifications exist.
- **AC-NOTIF-6**: Footer displays centered `"DONT FORGET · LOCAL HISTORY"`.
- **AC-NOTIF-7**: String resources extracted into `strings_notification_display.xml`.
