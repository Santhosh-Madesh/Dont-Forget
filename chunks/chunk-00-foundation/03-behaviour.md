# Behaviour — Chunk 00: Shared Foundation (`chunk-00-foundation`)

## 1. MVI Unidirectional Data Flow Conventions
- **UI State (`UiState`)**: Immutable data class representing screen state.
- **UI Event (`UiEvent`)**: User actions triggered from the UI sent to ViewModel.
- **UI Side Effect (`UiSideEffect`)**: One-time side effects emitted by ViewModel (e.g., `Navigate`, `ShowSnackbar`).

## 2. Data Access & Repositories Behaviour
- `ReminderRepository` methods emit `Flow` for reactive updates. When a checklist item's packed status is changed in the database, subscribers automatically receive updated state.
- `TemplateRepository` methods emit `Flow` for reactive updates of templates and contexts.

## 3. Auto-Purge Inactive Reminders
- On app launch, `ReminderRepository.deleteInactiveRemindersOlderThan(cutoffTime)` queries Room for reminders where `isCompleted == true` and `completedEpochMillis <= cutoffTime` (where `cutoffTime = currentTime - 24 hours`).
- Deletes those reminders and returns their IDs so scheduled alarms can be canceled.
