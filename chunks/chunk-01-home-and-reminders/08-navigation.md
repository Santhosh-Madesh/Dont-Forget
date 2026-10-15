# Navigation — Chunk 01: Home Feature (`chunk-01-home-and-reminders`)

## Route & Navigation Graph Wiring

### 1. Route Definition
- Route object: `Route.Home`
- Route string: `"home"`

### 2. Destination Composables & Callbacks in `MainActivity.kt`
```kotlin
composable(Route.Home.route) {
    val homeViewModel = remember { HomeViewModel(reminderRepo) }

    // Runtime Permission Check logic
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        val hasPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
        homeViewModel.onEvent(HomeUiEvent.OnNotificationPermissionStatusChanged(hasPermission))
    }

    HomeScreen(
        viewModel = homeViewModel,
        onNavigateToDetail = { reminderId ->
            navController.navigate(Route.ReminderDetail.createRoute(reminderId))
        },
        onNavigateToCreateReminder = {
            navController.navigate(Route.CreateReminder.route)
        },
        onOpenNotificationSettings = {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", packageName, null)
            }
            startActivity(intent)
        }
    )
}
```

### 3. Startup Coroutine (Auto Purge)
```kotlin
LaunchedEffect(Unit) {
    withContext(Dispatchers.IO) {
        val cutoffTime = System.currentTimeMillis() - (24 * 60 * 60 * 1000L)
        reminderRepo.deleteInactiveRemindersOlderThan(cutoffTime)
    }
}
```
