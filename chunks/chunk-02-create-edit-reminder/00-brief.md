# Brief — Chunk 02: Create & Edit Reminder Flow (`chunk-02-create-edit-reminder`)

follow `android-chunk-workflow`

## 1. Name & Scope
- **Name**: `chunk-02-create-edit-reminder`
- **Scope**: Implement the multi-step/single flow for creating and editing Reminders: Template selection (or skip), Context creation/selection (title + built-in Material Vector Icon picker), Checklist item creation/editing/reordering, Departure Date & Time pickers (validated against past date/time), Notification Lead Time selection (default 30 mins, custom picker), and Reminder Preview Screen (with prompt to save modified checklist as a new template, discard confirmation dialog, and creation confirmation).

## 2. Context & Architecture Pointers
- Read `docs/project-understanding.md`
- Read `docs/chunk-plan.md`
- Follow MVI ViewModel pattern using `ReminderRepository` and `TemplateRepository`.

## 3. Relevant Spec Sections & UI References
- Spec Section 4 (User Journeys 1, 2, 9, 10)
- Spec Section 6 (Requirements 1, 2, 10, 11)
- Spec Section 10 (Rules 7, 9, 10)
- UI References:
  - `Create Reminder - Select Template Screen.png`
  - `Create Reminder - Context Screen.png`
  - `Create Reminder - Checklist Screen.png`
  - `Create Reminder - Select departure and notification Screen.png`
  - `Create Reminder - Preview Screen.png`, `Dark Mode Create Reminder - Preview Screen.png`
  - `Custom Notification Screen.png`, `Discard Reminder Dialog.png`
  - `Reminder Edit Screen.png`

## 4. Contract Points & Dependencies
- **Consumes**: `ReminderRepository`, `TemplateRepository`, `Route.CreateReminder`, `Route.EditReminder(reminderId)`
- **Exposes**: `CreateReminderScreen`, `EditReminderScreen`
- **Navigation Out**: `Route.Home`, `Route.ReminderDetail(reminderId)`

## 5. Ownership Boundaries
- **May touch**: `com/example/dontforget/feature/createreminder/**`, `com/example/dontforget/feature/editreminder/**`
- **May NOT touch**: Home screen (`feature/home`), Template management screens (`feature/templates`), Notification scheduling (`feature/notifications`), Shared data layer (`data/**`).
