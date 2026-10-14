# Feature Contract — Chunk 00: Shared Foundation (`chunk-00-foundation`)

## 1. Goal
Provide the core architecture, data persistence (Room database & repositories), Jetpack Navigation setup, Material 3 design system theme, core UI components, and MVI base abstractions needed by all feature chunks in DONT FORGET.

## 2. Scope

### In Scope
- Gradle Version Catalog (`libs.versions.toml`) and `app/build.gradle.kts` dependency setup for Navigation Compose, Room (runtime, ktx, compiler with KSP/kapt/annotationProcessor), and Material Icons Extended.
- Room Database (`DontForgetDatabase`, version 1):
  - Entities: `ContextEntity`, `TemplateEntity`, `ReminderEntity`, `ChecklistItemEntity`
  - DAOs: `ContextDao`, `TemplateDao`, `ReminderDao`, `ChecklistItemDao`
  - Repositories: `ReminderRepository` & `TemplateRepository` (interfaces + Room implementations)
- MVI Base Infrastructure:
  - `UiState`, `UiEvent`, `UiSideEffect` interfaces
- Design System & Common UI:
  - Material 3 theme (`Color.kt`, `Type.kt`, `Theme.kt`, `Spacing.kt`) with Light & Dark mode support
  - Built-in Material Vector Icons provider/list for Context icons
  - Common UI components: `AppTopBar`, `WarningBanner`, `ConfirmationDialog`
- Navigation Setup:
  - `Route` sealed class hierarchy (`Home`, `CreateReminder`, `EditReminder`, `TemplateManagement`, `TemplateDetail`, `TemplateEdit`, `ReminderDetail`)
  - `MainActivity` setup with `NavHost` and placeholder routes.

### Out of Scope
- Screen-specific ViewModels and UI logic for Home, Create/Edit Reminder, Template Management, and Notifications (reserved for Chunks 1-4).

## 3. Data & Dependencies
- **Dependencies**:
  - `androidx.navigation:navigation-compose`
  - `androidx.room:room-runtime`, `androidx.room:room-ktx`, `androidx.room:room-compiler`
  - `androidx.compose.material:material-icons-extended`
- **Data Models**:
  - `ContextEntity` (id, title, iconName)
  - `TemplateEntity` (id, contextId, title)
  - `ReminderEntity` (id, contextId, contextTitle, contextIconName, departureEpochMillis, notificationLeadMinutes, isCompleted, createdEpochMillis, completedEpochMillis)
  - `ChecklistItemEntity` (id, parentId, parentType ["REMINDER" | "TEMPLATE"], text, isPacked, displayOrder)

## 4. Exposed Surface
- `ReminderRepository`:
  - `fun getActiveReminders(): Flow<List<ReminderWithItems>>`
  - `fun getReminderById(id: Long): Flow<ReminderWithItems?>`
  - `suspend fun insertReminder(reminder: ReminderEntity, items: List<ChecklistItemEntity>): Long`
  - `suspend fun updateReminder(reminder: ReminderEntity, items: List<ChecklistItemEntity>)`
  - `suspend fun updateChecklistItemPacked(itemId: Long, isPacked: Boolean)`
  - `suspend fun packAllItems(reminderId: Long)`
  - `suspend fun markReminderComplete(reminderId: Long, completedEpochMillis: Long)`
  - `suspend fun restoreReminder(reminderId: Long)`
  - `suspend fun deleteReminder(reminderId: Long)`
  - `suspend fun deleteInactiveRemindersOlderThan(cutoffEpochMillis: Long): List<Long>`
- `TemplateRepository`:
  - `fun getAllTemplates(): Flow<List<TemplateWithContextAndItems>>`
  - `fun getTemplateById(id: Long): Flow<TemplateWithContextAndItems?>`
  - `fun getAllContexts(): Flow<List<ContextEntity>>`
  - `suspend fun insertContext(context: ContextEntity): Long`
  - `suspend fun insertTemplate(template: TemplateEntity, items: List<ChecklistItemEntity>): Long`
  - `suspend fun updateTemplate(template: TemplateEntity, items: List<ChecklistItemEntity>)`
  - `suspend fun deleteTemplate(templateId: Long)`
- `Route`:
  - `Route.Home`
  - `Route.CreateReminder`
  - `Route.EditReminder(reminderId)`
  - `Route.TemplateManagement`
  - `Route.TemplateDetail(templateId)`
  - `Route.TemplateEdit(templateId)`
  - `Route.ReminderDetail(reminderId)`

## 5. Acceptance Criteria
- **AC-1**: Gradle project sync and Kotlin compilation succeed with the added dependencies (`room`, `navigation-compose`, `material-icons-extended`).
- **AC-2**: `DontForgetDatabase` successfully defines the Room schema and creates DAOs for Contexts, Templates, Reminders, and Checklist Items.
- **AC-3**: `ReminderRepository` provides reactive `Flow` and suspend functions for full CRUD and packing operations on Reminders.
- **AC-4**: `TemplateRepository` provides reactive `Flow` and suspend functions for full CRUD operations on Contexts and Templates.
- **AC-5**: `DontForgetTheme` correctly provides Material 3 color schemes, typography, and spacing for both Light and Dark modes.
- **AC-6**: `Route` sealed class defines all top-level and detail navigation routes.
- **AC-7**: `MainActivity` compiles and runs with Jetpack Compose `NavHost`.
