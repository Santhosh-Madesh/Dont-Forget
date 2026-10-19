# UI State — Chunk 06: Bottom Navigation (`chunk-06-bottom-navigation`)

## State Management

The bottom navigation bar is stateless and driven directly by the Navigation BackStack entry state in `MainActivity`:

```kotlin
val navBackStackEntry by navController.currentBackStackEntryAsState()
val currentDestination = navBackStackEntry?.destination
val currentRoute = currentDestination?.route

val isBottomBarVisible = currentRoute == Route.Home.route ||
        currentRoute == Route.TemplateManagement.route
```
