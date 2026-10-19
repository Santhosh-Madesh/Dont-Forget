# Behaviour — Chunk 06: Bottom Navigation (`chunk-06-bottom-navigation`)

## Interactive Behaviour & State Specifications

### 1. Tab Switching & Backstack Management
- When user taps "Reminders":
  - Navigates to `Route.Home.route`.
  - Pops up to start destination of graph to avoid stacking multiple Home instances.
  - Launches with `launchSingleTop = true` and `restoreState = true`.
- When user taps "Templates":
  - Navigates to `Route.TemplateManagement.route`.
  - Pops up to start destination of graph with `saveState = true`.
  - Launches with `launchSingleTop = true` and `restoreState = true`.

### 2. Visibility Handling
- As user navigates away from Home or Template Management into detail/creation screens (e.g. `Create Reminder`, `Reminder Detail`, `Edit Reminder`), the bottom navigation bar automatically disappears.
- When user navigates back to Home or Template Management, the bottom bar smoothly reappears.
