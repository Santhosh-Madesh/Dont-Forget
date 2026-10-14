# Feature Contract — Chunk 01: Reminders & Home Screen (`chunk-01-home-and-reminders`)

## 1. Goal
Provide the Home Screen displaying active reminders (or empty state) and the Reminder Overview/Detail Screen with interactive item packing, "Pack All", "Mark as Complete" flow, deletion, and restore capabilities.

## 2. Scope

### In Scope
- **Home Screen (`HomeScreen`)**:
  - Top app bar with app name and navigation button to Template Management.
  - Active reminders list sorted by departure date & time.
  - Reminder Card showing Context icon, Context title, departure date & time, packed items ratio (e.g., "3/5Packed").
  - Empty state view when no active reminders exist.
  - Floating Action Button (FAB) "+ New Reminder" navigating to `Route.CreateReminder`.
- **Reminder Detail / Overview Screen (`ReminderDetailScreen`)**:
  - Context header with Context icon, title, departure date & time, notification lead time.
  - Options menu / actions (Edit Reminder -> `Route.EditReminder(reminderId)`, Delete Reminder).
  - Checklist items list with interactive checkboxes (Packed / Unpacked).
  - "Pack All" CTA button to mark all items packed.
  - "Update" button to persist item packing changes.
  - "Mark as Complete" popup card / button (displayed when 100% of items are packed).
  - "Delete Reminder" confirmation dialog.
  - "Restore Reminder" flow for completed reminders (prompts user to mark at least 1 item unpacked to reactivate).

### Out of Scope
- Reminder creation/edit form inputs (Chunk 2).
- Template management screens (Chunk 3).
- System alarm triggers and notification permission requests (Chunk 4).

## 3. Data & Dependencies
- **Consumes**:
  - `ReminderRepository`:
    - `getActiveReminders(): Flow<List<ReminderWithItems>>`
    - `getReminderById(id: Long): Flow<ReminderWithItems?>`
    - `updateChecklistItemPacked(itemId: Long, isPacked: Boolean)`
    - `packAllItems(reminderId: Long)`
    - `markReminderComplete(reminderId: Long)`
    - `restoreReminder(reminderId: Long)`
    - `deleteReminder(reminderId: Long)`

## 4. Exposed Surface
- `HomeScreen` composable
- `ReminderDetailScreen` composable
- Navigation events to `Route.CreateReminder`, `Route.EditReminder(reminderId)`, `Route.TemplateManagement`

## 5. Acceptance Criteria
- **AC-1**: `HomeScreen` displays active reminders with context title, icon, formatted departure date/time, and packed status badge.
- **AC-2**: `HomeScreen` displays an empty state when no active reminders exist.
- **AC-3**: Tapping "+ New Reminder" emits navigation effect to `Route.CreateReminder`.
- **AC-4**: Tapping a reminder card on Home navigates to `ReminderDetailScreen(reminderId)`.
- **AC-5**: On `ReminderDetailScreen`, toggling checkboxes modifies item packed status. "Pack All" marks all items packed.
- **AC-6**: When all checklist items are packed, the "Mark as Complete" button/card is displayed.
- **AC-7**: Selecting "Mark as Complete" marks the reminder complete in repository and returns to Home.
- **AC-8**: Selecting "Delete" displays a delete confirmation dialog; confirming deletes the reminder and returns to Home.
- **AC-9**: Restoring a completed reminder requires marking at least 1 item unpacked and reactivates the reminder in repository.
