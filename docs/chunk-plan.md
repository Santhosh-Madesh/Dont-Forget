# Chunk Plan — DONT FORGET

This document divides the DONT FORGET project into independent feature chunks following `android-project-workflow`.

---

## Shared Foundation (`chunk-00-foundation`) — **STATUS: COMPLETED & APPROVED**
- **Scope**: Setup Gradle dependencies (`room`, `navigation-compose`, `material-icons-extended`), define design system tokens (colors, typography, light/dark themes, spacing, icon list), create core UI components (WarningBanner, AppTopBar, ConfirmationDialog), implement Room Database (`DontForgetDatabase`), data entities (`ContextEntity`, `TemplateEntity`, `ReminderEntity`, `ChecklistItemEntity`), DAOs, repository interfaces & implementations (`ReminderRepository`, `TemplateRepository`), MVI base pattern (`UiState`, `UiEvent`, `UiSideEffect`), navigation routes (`Route` sealed interface/class), and `MainActivity` navigation host setup with a sample placeholder screen.
- **Spec Sections**: Section 9 (Data), Section 10 (Rules), Section 8 (All screens - theme/nav).
- **Files Owned**:
  - `gradle/libs.versions.toml`, `app/build.gradle.kts`
  - `com/example/dontforget/core/**` (MVI, Navigation, UI Components, Theme)
  - `com/example/dontforget/data/**` (Room Database, Entities, DAOs, Repositories)
  - `com/example/dontforget/MainActivity.kt`
- **Interfaces Exposed**:
  - `ReminderRepository` (CRUD operations for Reminders & Checklist Items)
  - `TemplateRepository` (CRUD operations for Contexts & Templates)
  - `Route` sealed class (Destination routes for Navigation)
- **Status**: **Completed & Approved**

---

## Chunk 1: Reminders & Home Screen (`chunk-01-home-and-reminders`) — **STATUS: COMPLETED & APPROVED**
- **One-paragraph Scope**: Implements the Home Screen displaying active reminders, the Reminder Detail/Overview screen showing checklist items with interactive checkboxes, "Pack All" packing capability, "Mark as Complete" popup dialog, Reminder Restore flow for accidentally completed reminders, Reminder Deletion dialog, and automatic purging of inactive/completed reminders older than 24 hours on app launch.
- **Spec Sections**: User Journeys 4, 5, 7, 8; Requirements 3, 4, 11; Rules 1, 11; Screens 1 (Home), 9 (Reminder View), Delete / Mark Complete Popups.
- **Files Owned**: `com/example/dontforget/feature/home/**`, `com/example/dontforget/feature/reminderdetail/**`.
- **Interfaces Consumed**: `ReminderRepository`, `Route.Home`, `Route.ReminderDetail`.
- **Interfaces Exposed**: `HomeScreen`, `ReminderDetailScreen`.
- **Routes In/Out**:
  - In: `Route.Home`, `Route.ReminderDetail(reminderId)`
  - Out: `Route.CreateReminder`, `Route.EditReminder(reminderId)`, `Route.TemplateManagement`
- **Status**: **Completed & Approved**

---

## Chunk 2: Create & Edit Reminder Flow (`chunk-02-create-edit-reminder`) — **STATUS: READY TO START**
- **One-paragraph Scope**: Implements the complete multi-step creation and edit flow for Reminders: Template selection (or skip), Context creation/selection (title + built-in Material Vector Icon picker), Checklist item addition/editing/reordering, Departure Date & Time selection (with past date/time validation), Notification Lead Time selection (default 30 mins, custom lead time picker), and Reminder Preview screen (with prompt to save modified checklist as a new template, discard confirmation dialog, and create confirmation).
- **Spec Sections**: User Journeys 1, 2, 9, 10; Requirements 1, 2, 10, 11; Rules 7, 9, 10; Screens 2 (Template), 3 (Context), 4 (Checklist), 5 (Departure Date/Time), 6 (Notification Time), 7 (Reminder Preview), 10 (Reminder Edit).
- **Files Owned**: `com/example/dontforget/feature/createreminder/**`, `com/example/dontforget/feature/editreminder/**`.
- **Interfaces Consumed**: `ReminderRepository`, `TemplateRepository`, `Route.CreateReminder`, `Route.EditReminder`.
- **Interfaces Exposed**: `CreateReminderScreen`, `EditReminderScreen`.
- **Routes In/Out**:
  - In: `Route.CreateReminder`, `Route.EditReminder(reminderId)`
  - Out: `Route.Home`, `Route.ReminderDetail(reminderId)`
- **Status**: **Ready to start**

---

## Chunk 3: Template Management (`chunk-03-template-management`) — **STATUS: READY TO START**
- **One-paragraph Scope**: Implements Template Management features allowing users to view all available templates (grouped or listed by Context), view Template details, create a new Template from scratch, edit existing Templates (context title, checklist items, re-order items), and delete Templates with confirmation dialogs. Enforces template isolation so mutating or deleting a template never alters existing reminders created from it.
- **Spec Sections**: User Journeys 1, 3; Requirements 5, 6, 7, 8, 12, 13, 14, 15; Rules 2, 3, 4, 8; Screens 8 (Template Management), 11 (Template View), 12 (Template Edit).
- **Files Owned**: `com/example/dontforget/feature/templates/**`.
- **Interfaces Consumed**: `TemplateRepository`, `Route.TemplateManagement`, `Route.TemplateDetail`, `Route.TemplateEdit`.
- **Interfaces Exposed**: `TemplateManagementScreen`, `TemplateDetailScreen`, `TemplateEditScreen`.
- **Routes In/Out**:
  - In: `Route.TemplateManagement`, `Route.TemplateDetail(templateId)`, `Route.TemplateEdit(templateId)`
  - Out: `Route.Home`
- **Status**: **Ready to start**

---

## Chunk 4: Notifications & Deep Linking (`chunk-04-notifications-scheduling`) — **STATUS: READY TO START**
- **One-paragraph Scope**: Implements exact alarm scheduling using `AlarmManager`, `ReminderAlarmReceiver` broadcast receiver, notification creation via `NotificationManager`, runtime permission request for `POST_NOTIFICATIONS` (Android 13+), warning banner & dialog prompting user to enable permissions in system settings (per UI spec), deep linking handling when clicking notification CTA ("Check Checklist") to navigate directly to `ReminderOverview`, and notification cancellation when reminders are completed or deleted.
- **Spec Sections**: User Journeys 6; Requirements 9, 10, 12; Edge Cases 5, 10; Screen: Notification Warning Dialog.
- **Files Owned**: `com/example/dontforget/feature/notifications/**`.
- **Interfaces Consumed**: `ReminderRepository`, `NotificationScheduler`, `Route.ReminderDetail`.
- **Interfaces Exposed**: `NotificationScheduler`, `ReminderAlarmReceiver`, `NotificationPermissionHandler`.
- **Routes In/Out**:
  - Deep Link Intent -> `Route.ReminderDetail(reminderId)`.
- **Status**: **Ready to start**

---

## Summary of Run Order
1. **Chunk 0: Shared Foundation** (**COMPLETED**)
2. **Chunk 1: Home & Reminders** (**COMPLETED**)
3. **Parallel Phase (Chunks 2, 3, 4)**:
   - Chunk 2: Create & Edit Reminder (**READY TO START**)
   - Chunk 3: Template Management (**READY TO START**)
   - Chunk 4: Notifications & Deep Linking (**READY TO START**)
