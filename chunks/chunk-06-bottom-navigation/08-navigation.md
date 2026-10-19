# Navigation — Chunk 06: Bottom Navigation (`chunk-06-bottom-navigation`)

## Navigation & Layout Integration (`MainActivity.kt`)

### Root `Scaffold` Setup in `MainActivity.kt`:

```kotlin
val navController = rememberNavController()
val navBackStackEntry by navController.currentBackStackEntryAsState()
val currentRoute = navBackStackEntry?.destination?.route

val showBottomBar = currentRoute in listOf(
    Route.Home.route,
    Route.TemplateManagement.route
)

Scaffold(
    bottomBar = {
        if (showBottomBar) {
            AppBottomBar(
                currentRoute = currentRoute,
                onNavItemClick = { item ->
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
) { innerPadding ->
    NavHost(
        navController = navController,
        startDestination = Route.Home.route,
        modifier = Modifier.padding(innerPadding)
    ) {
        // Destination composables...
    }
}
```
