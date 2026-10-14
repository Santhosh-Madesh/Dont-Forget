# Brief — Chunk 04: Notifications & Deep Linking (`chunk-04-notifications-scheduling`)

follow `android-chunk-workflow`

## 1. Name & Scope
- **Name**: `chunk-04-notifications-scheduling`
- **Scope**: Implement exact alarm scheduling using `AlarmManager`, `ReminderAlarmReceiver` broadcast receiver, notification creation via `NotificationManager`, runtime permission request for `POST_NOTIFICATIONS` (Android 13+), warning banner & dialog prompting user to enable permissions in system settings (per UI spec `Create Reminder - Select departure and notification warning dialog Screen.png`), deep linking handling when clicking notification CTA ("Check Checklist") to navigate directly to `ReminderOverview`, and notification cancellation when reminders are completed or deleted.

## 2. Context & Architecture Pointers
- Read `docs/project-understanding.md`
- Read `docs/chunk-plan.md`
- Uses `ReminderRepository` and Android system alarm/notification services.

## 3. Relevant Spec Sections & UI References
- Spec Section 4 (User Journeys 6)
- Spec Section 6 (Requirements 9, 10, 12)
- Spec Section 7 (Edge Cases 5, 10)
- UI References: `Create Reminder - Select departure and notification warning dialog Screen.png`

## 4. Contract Points & Dependencies
- **Consumes**: `ReminderRepository`, `Route.ReminderDetail(reminderId)`
- **Exposes**: `NotificationScheduler`, `ReminderAlarmReceiver`, `NotificationPermissionHandler`
- **Deep Links**: Intent target -> `Route.ReminderDetail(reminderId)`

## 5. Ownership Boundaries
- **May touch**: `com/example/dontforget/feature/notifications/**`
- **May NOT touch**: UI screens (`feature/home`, `feature/createreminder`, `feature/templates`), Shared data layer (`data/**`).
