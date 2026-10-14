# Test Specification — Chunk 02: Create & Edit Reminder Flow

- `CreateReminderViewModelTest`:
  - `testStepProgressionValidation`: Cannot move past Context with empty title; cannot move past Checklist with empty items; cannot move past Date/Time with past date.
  - `testTemplateSelection`: Pre-fills Context Title, Icon, and Checklist items from chosen template.
  - `testCustomNotificationLeadTime`: Correctly parses user custom lead time input.
  - `testSaveReminderAndTemplate`: Saves new reminder and creates new template if `saveAsTemplate` is true.
- `EditReminderViewModelTest`:
  - `testLoadReminder`: Correctly populates state from repository.
  - `testValidation`: Prevents saving if checklist is empty or departure time in past.
  - `testSaveChanges`: Calls `ReminderRepository.updateReminder` with updated items.
