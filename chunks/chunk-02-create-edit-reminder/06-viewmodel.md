# ViewModel Architecture — Chunk 02: Create & Edit Reminder Flow

- `CreateReminderViewModel`: Extends `BaseViewModel<CreateReminderUiState, CreateReminderUiEvent, CreateReminderUiSideEffect>`.
  - Injects `ReminderRepository` and `TemplateRepository`.
  - Observes templates flow for Step 1.
  - Manages wizard step progression with validation guards.
  - Handles insertion of Context, Reminder, Reminder Items, and optionally Template & Template Items.
- `EditReminderViewModel`: Extends `BaseViewModel<EditReminderUiState, EditReminderUiEvent, EditReminderUiSideEffect>`.
  - Injects `ReminderRepository`.
  - Loads reminder by ID, maintains dirty state (`isModified`), performs validation, and executes `updateReminder`.
