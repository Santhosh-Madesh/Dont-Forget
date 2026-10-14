# Unit Tests — Chunk 01: Reminders & Home Screen (`chunk-01-home-and-reminders`)

## 1. Planned Unit Tests
- `HomeViewModelTest`:
  - `initial state loads active reminders from repository`
  - `clicking reminder card emits NavigateToDetail effect`
  - `clicking FAB emits NavigateToCreateReminder effect`
  - `clicking templates icon emits NavigateToTemplates effect`
- `ReminderDetailViewModelTest`:
  - `loads reminder with checklist items`
  - `toggling item packing updates local state`
  - `clicking pack all marks all items packed and shows mark complete card`
  - `clicking mark complete marks reminder complete and navigates back`
  - `confirming delete deletes reminder and navigates back`
  - `restoring completed reminder with 0 unpacked items shows warning`

## 2. Test File Locations
- `app/src/test/java/com/example/dontforget/feature/home/HomeViewModelTest.kt`
- `app/src/test/java/com/example/dontforget/feature/reminderdetail/ReminderDetailViewModelTest.kt`
