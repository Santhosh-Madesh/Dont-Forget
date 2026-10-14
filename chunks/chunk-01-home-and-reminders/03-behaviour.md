# Behaviour — Chunk 01: Reminders & Home Screen (`chunk-01-home-and-reminders`)

## 1. Home Screen Behaviour
- `HomeViewModel` subscribes to `ReminderRepository.getActiveReminders()`.
- Whenever active reminders change in Room, `HomeUiState.reminders` updates automatically.
- User clicking a reminder card triggers `OnReminderClick(reminderId)` event -> emits `NavigateToDetail(reminderId)` effect.
- User clicking FAB triggers `OnCreateReminderClick` event -> emits `NavigateToCreateReminder` effect.
- User clicking Templates icon triggers `OnTemplatesClick` event -> emits `NavigateToTemplates` effect.

## 2. Reminder Detail Behaviour
- `ReminderDetailViewModel` observes `ReminderRepository.getReminderById(reminderId)`.
- Checklist items maintain local state until "Update" or "Pack All" or checkbox toggle is performed.
- Toggling a checkbox updates item's `isPacked` status in repository via `updateChecklistItemPacked(itemId, isPacked)`.
- Clicking "Pack All" calls `ReminderRepository.packAllItems(reminderId)`.
- When all items are packed (`items.all { it.isPacked }`), `isAllPacked` in UI state becomes `true`, showing "Mark as Complete".
- Clicking "Mark as Complete" calls `ReminderRepository.markReminderComplete(reminderId)` and emits `NavigateBack` effect.
- Clicking "Delete" shows delete confirmation dialog. On confirmation, calls `ReminderRepository.deleteReminder(reminderId)` and emits `NavigateBack`.
