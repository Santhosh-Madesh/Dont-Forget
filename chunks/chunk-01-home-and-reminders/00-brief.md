# Brief — Chunk 01: Reminders & Home Screen (`chunk-01-home-and-reminders`)

follow `android-chunk-workflow`

## 1. Name & Scope
- **Name**: `chunk-01-home-and-reminders`
- **Scope**: Implement the Home Screen displaying today's date header (day, month, date), "Reminders" section header, notification permission warning card (displayed ONLY if notification permissions are NOT allowed), Active vs Completed reminders filter toggle, categorized active reminders list ("Today", "Tomorrow", and "Upcoming" categories based on departure date), completed reminders list, Floating Action Button for creating reminders, Top App Bar with mock notification icon, Reminder Overview/Detail Screen with interactive checklist packing/unpacking checkboxes, "Pack All" packing capability, "Mark as Complete" popup card, Reminder Restore flow for accidentally completed reminders, Reminder Deletion dialog, and automatic purging of inactive/completed reminders older than 24 hours on app launch.

## 2. Context & Architecture Pointers
- Read `docs/project-understanding.md`
- Read `docs/chunk-plan.md`
- Follow MVI ViewModel pattern (`UiState`, `UiEvent`, `UiSideEffect`) using `ReminderRepository`.

## 3. Relevant Spec Sections & UI References
- Spec Section 4 (User Journeys 4, 5, 7, 8)
- Spec Section 6 (Requirements 3, 4, 11)
- Spec Section 10 (Rules 1, 11)
- UI References:
  - `Home Screen.png`, `Empty Home Screen.png`, `Dark Mode Home Screen.png`
  - `Reminder Detail Screen.png`, `Dark Mode Reminder Detail Screen.png`
  - `Mark as Completed pop up card.png`, `Reminder Restore pop up Card.png`, `Delete Reminder Dialog.png`

## 4. Contract Points & Dependencies
- **Consumes**: `ReminderRepository`, `Route.Home`, `Route.ReminderDetail(reminderId)`
- **Exposes**: `HomeScreen`, `ReminderDetailScreen`
- **Navigation Out**: `Route.CreateReminder`, `Route.EditReminder(reminderId)` (Top app bar features a mock notification icon button instead of Template Management shortcut)

## 5. Ownership Boundaries
- **May touch**: `com/example/dontforget/feature/home/**`, `com/example/dontforget/feature/reminderdetail/**`
- **May NOT touch**: Template management (`feature/templates`), Create/Edit flow (`feature/createreminder`), Notification receivers (`feature/notifications`), Shared data layer (`data/**`).
