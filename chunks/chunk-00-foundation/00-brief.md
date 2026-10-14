# Brief — Chunk 00: Shared Foundation (`chunk-00-foundation`)

follow `android-chunk-workflow`

## 1. Name & Scope
- **Name**: `chunk-00-foundation`
- **Scope**: Setup Gradle version catalog and build script dependencies (`room`, `navigation-compose`, `material-icons-extended`). Build the shared Material 3 design system theme (Color, Type, Theme, Spacing, Icon set, common UI components like AppTopBar, WarningBanner, ConfirmationDialog). Implement the Room Database (`DontForgetDatabase`), data entities (`ContextEntity`, `TemplateEntity`, `ReminderEntity`, `ChecklistItemEntity`), DAOs, repository interfaces & implementations (`ReminderRepository`, `TemplateRepository`), MVI base interfaces (`UiState`, `UiEvent`, `UiSideEffect`), Navigation routes (`Route` sealed interface/class), and `MainActivity` setup.

## 2. Context & Architecture Pointers
- Read `docs/project-understanding.md`
- Read `docs/chunk-plan.md`
- Architectural pattern: MVVM with Compose MVI (`UiState`, `UiEvent`, `UiSideEffect`), Room Database for local storage, Jetpack Navigation Compose.

## 3. Relevant Spec Sections & UI References
- Spec Section 8 (Screens Overview & Theme)
- Spec Section 9 (Data Entities & Models)
- Spec Section 10 (Rules & Constraints)
- UI References: `Dark Mode Home Screen.png`, `Home Screen.png`

## 4. Contract Points & Interfaces
- **Exposes**:
  - Entities: `ContextEntity`, `TemplateEntity`, `ReminderEntity`, `ChecklistItemEntity`
  - Repositories: `ReminderRepository`, `TemplateRepository`
  - Navigation Routes: `Route.Home`, `Route.CreateReminder`, `Route.EditReminder`, `Route.TemplateManagement`, `Route.TemplateDetail`, `Route.TemplateEdit`, `Route.ReminderDetail`
  - Components: `DontForgetTheme`, `AppTopBar`, `WarningBanner`, `ConfirmationDialog`

## 5. Ownership Boundaries
- **May touch**:
  - `gradle/libs.versions.toml`, `app/build.gradle.kts`
  - `com/example/dontforget/core/**`
  - `com/example/dontforget/data/**`
  - `com/example/dontforget/ui/theme/**`
  - `com/example/dontforget/MainActivity.kt`
- **May NOT touch**: Feature-specific screens or logic (reserved for Chunks 1-4).
