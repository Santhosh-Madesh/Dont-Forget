# Feature Contract — Chunk 03: Template Management (`chunk-03-template-management`)

## 1. Goal
Provide Template Management features allowing users to view, create, edit, and delete reusable checklist Templates independently from Reminders.

## 2. Scope

### In Scope
- **Template Management Screen (`TemplateManagementScreen`)**:
  - Displays all available templates grouped by Context (Context title, icon, and template list).
  - Empty state when no templates exist ("No Templates Yet" graphic + CTA "Create Template").
  - FAB "+ New Template" navigating to `TemplateEditScreen(templateId = 0)`.
- **Template Detail Screen (`TemplateDetailScreen`)**:
  - Displays Template Context icon, Context title, Template title, and checklist items list.
  - Top bar actions: Edit (`Route.TemplateEdit(templateId)`) and Delete with confirmation dialog.
- **Template Edit Screen (`TemplateEditScreen`)**:
  - Create new template or edit existing template.
  - Context selection (pick existing Context or enter new Context title & pick Material Vector icon).
  - Template title input field.
  - Checklist item manager (add item, remove item, edit text, reorder items).
  - Save CTA button: validates required fields and persists template + items to `TemplateRepository`.
- **Template Isolation Rule**:
  - Mutating or deleting a template NEVER mutates or deletes any existing Reminders created from it.

### Out of Scope
- Creating or editing Reminders (Chunk 2).
- Notification scheduling (Chunk 4).

## 3. Data & Dependencies
- **Consumes**:
  - `TemplateRepository`:
    - `getAllTemplates(): Flow<List<TemplateWithContextAndItems>>`
    - `getTemplateById(id: Long): Flow<TemplateWithContextAndItems?>`
    - `getAllContexts(): Flow<List<ContextEntity>>`
    - `insertContext(context: ContextEntity): Long`
    - `insertTemplate(template: TemplateEntity, items: List<ChecklistItemEntity>): Long`
    - `updateTemplate(template: TemplateEntity, items: List<ChecklistItemEntity>)`
    - `deleteTemplate(templateId: Long)`

## 4. Exposed Surface
- `TemplateManagementScreen`
- `TemplateDetailScreen`
- `TemplateEditScreen`
- Routes: `Route.TemplateManagement`, `Route.TemplateDetail(templateId)`, `Route.TemplateEdit(templateId)`

## 5. Acceptance Criteria
- **AC-1**: `TemplateManagementScreen` displays templates grouped by Context with icon, title, and item count.
- **AC-2**: `TemplateManagementScreen` displays empty state when no templates exist.
- **AC-3**: Tapping a template card opens `TemplateDetailScreen(templateId)`.
- **AC-4**: On `TemplateDetailScreen`, tapping "Edit" opens `TemplateEditScreen(templateId)`.
- **AC-5**: On `TemplateDetailScreen`, confirming deletion deletes the template from `TemplateRepository` without modifying existing reminders.
- **AC-6**: `TemplateEditScreen` allows creating/editing template title, selecting Context & icon, adding/removing checklist items, and saving.
- **AC-7**: Validation prevents saving a template with blank title, no context, or 0 checklist items.
