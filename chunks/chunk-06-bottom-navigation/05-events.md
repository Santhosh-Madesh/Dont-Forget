# Events — Chunk 06: Bottom Navigation (`chunk-06-bottom-navigation`)

## Events Contract

Navigation bar item click event:

```kotlin
fun onBottomNavItemClicked(item: BottomNavItem, navController: NavHostController) {
    if (navController.currentDestination?.route != item.route) {
        navController.navigate(item.route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
}
```
