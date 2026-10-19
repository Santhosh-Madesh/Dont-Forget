# Tests — Chunk 06: Bottom Navigation (`chunk-06-bottom-navigation`)

## Test Plan

1. **`AppBottomBarTest`**:
   - Verify "Reminders" and "Templates" items are displayed.
   - Verify correct item is marked selected based on `currentRoute`.
   - Verify clicking "Templates" item triggers callback with `BottomNavItem.Templates`.
   - Verify clicking "Reminders" item triggers callback with `BottomNavItem.Reminders`.

2. **Navigation Integration Test**:
   - Verify bottom bar is visible on Home route (`"home"`).
   - Verify bottom bar is visible on Template Management route (`"template_management"`).
   - Verify bottom bar is hidden on Reminder Detail route (`"reminder_detail/{reminderId}"`).
