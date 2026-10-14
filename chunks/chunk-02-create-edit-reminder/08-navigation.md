# Navigation Specification — Chunk 02: Create & Edit Reminder Flow

- `Route.CreateReminder.route`: Navigates to `CreateReminderScreen`.
- `Route.EditReminder.ROUTE`: Navigates to `EditReminderScreen` with `reminderId` argument.
- Navigation out on save/discard:
  - Discard/Cancel -> Back to `Route.Home`.
  - Save success -> Navigate to `Route.ReminderDetail(reminderId)` or `Route.Home`.
