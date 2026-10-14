# Brief — Chunk 03: Template Management (`chunk-03-template-management`)

follow `android-chunk-workflow`

## 1. Name & Scope
- **Name**: `chunk-03-template-management`
- **Scope**: Implement Template Management features allowing users to view all available templates (grouped/listed by Context), view Template details, create a new Template from scratch, edit existing Templates (context title, checklist items add/remove/reorder), and delete Templates with confirmation dialogs. Ensures template isolation so mutating or deleting a template never alters existing reminders created from it.

## 2. Context & Architecture Pointers
- Read `docs/project-understanding.md`
- Read `docs/chunk-plan.md`
- Follow MVI ViewModel pattern using `TemplateRepository`.

## 3. Relevant Spec Sections & UI References
- Spec Section 4 (User Journeys 1, 3)
- Spec Section 6 (Requirements 5, 6, 7, 8, 12, 13, 14, 15)
- Spec Section 10 (Rules 2, 3, 4, 8)
- UI References:
  - `Template Management Screen.png`, `Empty Template Management Screen.png`
  - `Template Detail Screen.png`
  - `Template Edit Screen.png`

## 4. Contract Points & Dependencies
- **Consumes**: `TemplateRepository`, `Route.TemplateManagement`, `Route.TemplateDetail(templateId)`, `Route.TemplateEdit(templateId)`
- **Exposes**: `TemplateManagementScreen`, `TemplateDetailScreen`, `TemplateEditScreen`
- **Navigation Out**: `Route.Home`

## 5. Ownership Boundaries
- **May touch**: `com/example/dontforget/feature/templates/**`
- **May NOT touch**: Home screen (`feature/home`), Reminder Creation flow (`feature/createreminder`), Notifications (`feature/notifications`), Shared data layer (`data/**`).
