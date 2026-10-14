# Unit Tests — Chunk 00: Shared Foundation (`chunk-00-foundation`)

## 1. Planned Test Classes
- `BaseViewModelTest`: Verifies initial state, state update reducer, and effect channel emissions in `BaseViewModel`.
- `ReminderRepositoryTest`: Verifies CRUD, packing, completing, restoring, and auto-purging inactive reminders >= 24h.
- `TemplateRepositoryTest`: Verifies CRUD operations for Contexts and Templates.

## 2. Test Files Location
- `app/src/test/java/com/example/dontforget/core/mvi/BaseViewModelTest.kt`
- `app/src/test/java/com/example/dontforget/data/repository/ReminderRepositoryTest.kt`
- `app/src/test/java/com/example/dontforget/data/repository/TemplateRepositoryTest.kt`
