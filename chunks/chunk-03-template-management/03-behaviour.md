# Behaviour — Chunk 03: Template Management (`chunk-03-template-management`)

## 1. Template Management Screen (`TemplateManagementScreen`)
- **Repository Subscription**:
  - `TemplateManagementViewModel` observes `TemplateRepository.getAllTemplates()`.
  - Reactively updates state whenever templates or contexts change in the Room database.
- **Grouping Logic**:
  - Templates are grouped by their associated `ContextEntity` (`ContextTemplateGroup(context, templates)`).
  - Groups are sorted alphabetically by `context.title`.
- **Empty State Behaviour**:
  - If `templates.isEmpty()`, renders `EmptyTemplateContent` ("No Templates Yet" icon, text description, and "Create Template" button).
- **Navigation Action**:
  - Clicking a template card emits `OnTemplateClick(templateId)` -> sends `NavigateToDetail(templateId)` side effect.
  - Clicking the FAB "+ New Template" or the empty state CTA button emits `OnCreateTemplateClick` -> sends `NavigateToCreate` side effect.

## 2. Template Detail Screen (`TemplateDetailScreen`)
- **Data Loading**:
  - `TemplateDetailViewModel` observes `TemplateRepository.getTemplateById(templateId)`.
  - Displays context icon, context title, template title, and checklist items list.
- **Template Isolation & Deletion Rule**:
  - Confirming template deletion calls `TemplateRepository.deleteTemplate(templateId)`.
  - Deletion removes the `TemplateEntity` and its checklist items (`parentType = "TEMPLATE"`).
  - Existing `ReminderEntity` and reminder checklist items (`parentType = "REMINDER"`) created from this template remain completely untouched.
  - Sends `NavigateBack` side effect upon deletion.

## 3. Template Edit Screen (`TemplateEditScreen`)
- **Create vs Edit Mode**:
  - When `templateId == 0L`, initializes form for creation ("Create Template").
  - When `templateId > 0L`, loads existing template data ("Edit Template").
- **Context Selection**:
  - Observes `TemplateRepository.getAllContexts()`.
  - User can select an existing context or toggle "+ Create New Context".
  - When creating a new context, accepts a custom context title and an icon chosen from `ContextIcons.AVAILABLE_ICONS`.
- **Checklist Item Manager**:
  - Add item: enters text in input field and clicks Add button / presses enter (`OnAddChecklistItem`).
  - Inline edit: modifies text in place (`OnEditChecklistItem`).
  - Delete item: removes item from local list (`OnRemoveChecklistItem`).
  - Reorder items: moves items up or down in list order (`OnReorderChecklistItem`).
- **Validation & Persistence**:
  - On clicking "Save Template":
    - Validates non-blank template title, non-blank context title, and at least 1 checklist item.
    - If validation fails, displays warning message.
    - If validation passes, inserts or updates `ContextEntity`, `TemplateEntity`, and `ChecklistItemEntity` list via `TemplateRepository`, then emits `NavigateBack` side effect.
- **Discard Dialog**:
  - Clicking back when unsaved changes exist prompts a confirmation dialog (`Discard Changes`). Confirming discards changes and navigates back.
