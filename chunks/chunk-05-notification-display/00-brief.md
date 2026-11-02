# Brief — Chunk 05: Notification Display Screen (`chunk-05-notification-display`)

follow `android-chunk-workflow`

## 1. Name & Scope
- **Name**: `chunk-05-notification-display`
- **Scope**: Implement the dedicated Notification Display Screen for the `Dont Forget` application. Displays all notifications categorized into "UPCOMING" (future trigger time) and "PREVIOUS" (past trigger time) sections. Features top app bar back navigation, category header inline badge counts, identical notification builder content text, centered empty state illustration when no notifications exist, and footer history label.

## 2. Context & Architecture Pointers
- Read `docs/project-understanding.md`
- Read `docs/chunk-plan.md`
- Follow MVI ViewModel pattern (`UiState`, `UiEvent`, `UiSideEffect`) using `ReminderRepository`.

## 3. Relevant Spec Sections & UI References
- UI Reference: `notification_screen_ui.png`

## 4. Contract Points & Dependencies
- **Consumes**: `ReminderRepository`, `Route.Notification`
- **Exposes**: `NotificationScreen`, `NotificationViewModel`
- **Navigation Out**: `Route.Home` (via Top app bar back icon / `onNavigateBack`)

## 5. Ownership Boundaries
- **May touch**: `com/example/dontforget/feature/notifications/screen/**`, `app/src/main/res/values/strings_notification_display.xml`
- **May NOT touch**: Background alarm scheduling (`notification/**`), Template management (`feature/templates`), Create/Edit flow (`feature/createreminder`).
