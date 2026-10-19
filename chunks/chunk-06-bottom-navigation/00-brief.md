# Brief — Chunk 06: Bottom Navigation (`chunk-06-bottom-navigation`)

follow `android-chunk-workflow`

## 1. Name & Scope
- **Name**: `chunk-06-bottom-navigation`
- **Scope**: Implement the primary bottom navigation bar for the app featuring two items: "Reminders" and "Templates". The bottom bar must only be displayed on top-level screens (`Home` / `Reminders` screen and `Template Management` screen) and hidden on all detail, creation, and edit screens. Clicking "Reminders" navigates to the Home screen, while clicking "Templates" navigates to the Template Management screen using single-top and state restoration patterns.

## 2. Context & Architecture Pointers
- Read `docs/project-understanding.md`
- Read `docs/chunk-plan.md`
- Follow Jetpack Compose Material 3 `NavigationBar` and `NavigationBarItem` patterns.
- Driven by `NavHostController.currentBackStackEntryAsState()` in `MainActivity.kt` or root Scaffold layout.

## 3. Relevant Spec Sections & UI References
- Spec Section 4 (User Journeys 1, 2)
- Spec Section 6 (Requirements 1, 2)
- UI References: `Home Screen.png`, `Template Management Screen.png`, `Dark Mode Home Screen.png`

## 4. Contract Points & Dependencies
- **Consumes**: `Route.Home`, `Route.TemplateManagement`, `navController`
- **Exposes**: `AppBottomBar`, `BottomNavItem`
- **Navigation Options**: `popUpTo(startDestination) { saveState = true }`, `launchSingleTop = true`, `restoreState = true`

## 5. Ownership Boundaries
- **May touch**: `core/ui/components/AppBottomBar.kt`, `MainActivity.kt` (scaffold & nav hierarchy)
- **May NOT touch**: Feature screens internal view models (`feature/createreminder`, `feature/editreminder`, `feature/reminderdetail`), Database layer (`data/**`), Notification logic (`notification/**`).
