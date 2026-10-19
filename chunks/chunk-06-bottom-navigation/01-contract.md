# Contract — Chunk 06: Bottom Navigation (`chunk-06-bottom-navigation`)

## Feature Contracts & Data Models

### 1. `BottomNavItem` Enum / Sealed Class
Defines the bottom navigation tabs, their associated routes, label resource / string, and icons.

```kotlin
package com.example.dontforget.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Reminders : BottomNavItem(
        route = Route.Home.route,
        title = "Reminders",
        selectedIcon = Icons.Filled.Notifications,
        unselectedIcon = Icons.Outlined.Notifications
    )

    object Templates : BottomNavItem(
        route = Route.TemplateManagement.route,
        title = "Templates",
        selectedIcon = Icons.Filled.GridView,
        unselectedIcon = Icons.Outlined.GridView
    )

    companion object {
        val items = listOf(Reminders, Templates)
    }
}
```

---

### 2. Visibility Contract
The Bottom Bar is visible ONLY if current destination route matches one of the top-level routes:
- `Route.Home.route` (`"home"`)
- `Route.TemplateManagement.route` (`"template_management"`)

Hidden for:
- `Route.CreateReminder`
- `Route.EditReminder`
- `Route.ReminderDetail`
- `Route.TemplateDetail`
- `Route.TemplateEdit`
