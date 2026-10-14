# Navigation — Chunk 01: Reminders & Home Screen (`chunk-01-home-and-reminders`)

`HomeScreen` wires to `Route.Home`.
`ReminderDetailScreen` wires to `Route.ReminderDetail(reminderId)`.

In `MainActivity.kt`:
- `composable(Route.Home.route)` renders `HomeScreen(...)`
- `composable(Route.ReminderDetail.ROUTE, arguments = ...)` renders `ReminderDetailScreen(...)`
