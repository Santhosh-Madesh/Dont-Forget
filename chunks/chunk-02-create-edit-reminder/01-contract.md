# Feature Contract — Chunk 02: Create & Edit Reminder Flow (`chunk-02-create-edit-reminder`)

## 1. Goal
Provide the complete user flow and business logic for creating new reminders (via a 5-step wizard with optional template pre-filling) and editing existing reminders, enforcing strict input validation and template isolation.

## 2. Scope

### In Scope
- **Create Reminder Wizard**:
  - Step 1: Select existing Template or start custom reminder.
  - Step 2: Context Title & Icon selection (`ContextIcons`).
  - Step 3: Add, edit, remove, and reorder checklist items.
  - Step 4: Departure Date & Time picker + Notification Lead Time picker (with custom minutes input).
  - Step 5: Reminder Preview screen with "Save modified checklist as a new template?" option, create confirmation, and discard confirmation.
- **Edit Reminder Screen**:
  - Single-screen form pre-populated with existing reminder data by `reminderId`.
  - Context, Checklist, Departure Date/Time, and Notification Lead Time modification.
  - Discard dialog on cancellation.
- **Validation Rules**:
  - Context Title cannot be empty.
  - At least 1 checklist item required.
  - Departure Date & Time must be in the future (> current time).
  - Notification Lead Time must be valid (> 0 mins).
- **Template Isolation**:
  - Editing a reminder created from a template does NOT mutate the original template unless explicitly saved as a new template on Preview.

### Out of Scope
- Home screen display & packing toggles (Chunk 01).
- Template management & template creation outside reminder creation (Chunk 03).
- Notification alarms & system notification posting (Chunk 04).

## 3. Interfaces Consumed & Exposed
- **Consumes**: `ReminderRepository`, `TemplateRepository`, `Route.CreateReminder`, `Route.EditReminder(reminderId)`.
- **Exposes**: `CreateReminderScreen`, `EditReminderScreen`.
- **Navigation Out**: `Route.Home`, `Route.ReminderDetail(reminderId)`.
