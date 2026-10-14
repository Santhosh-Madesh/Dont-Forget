# Behaviour Specification — Chunk 02: Create & Edit Reminder Flow

## 1. Multi-Step Navigation & Validation Logic
1. **Step Transition**:
   - Step 1 -> Step 2: Selecting template pre-fills Context Title, Icon, and Checklist items. Clicking "Skip" initializes blank context and empty checklist.
   - Step 2 -> Step 3: Validates Context Title is non-blank.
   - Step 3 -> Step 4: Validates Checklist has >= 1 item.
   - Step 4 -> Step 5: Validates Departure Date & Time > System.currentTimeMillis().
2. **Save Reminder & Template Creation**:
   - Creates Context in DB if new.
   - Creates ReminderEntity with calculated departure epoch millis and notification lead minutes.
   - Creates ChecklistItemEntities with `parentType = "REMINDER"`.
   - If "Save as template" is checked: Creates TemplateEntity and ChecklistItemEntities with `parentType = "TEMPLATE"`.
3. **Discard Dialog**:
   - Show `ConfirmationDialog` if user cancels or navigates back with unsaved changes.
