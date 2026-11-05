# Navigation — Onboarding Feature (`onboarding-chunk`)

## 1. Route Entry
- Class / Route: `Route.Onboarding` in `Route.kt`.

## 2. NavHost Setup (`MainActivity`)
- `PreferencesViewModel` checks `isOnboardingCompleted`.
- Start destination: `if (isOnboardingCompleted == true) Route.Home else Route.Onboarding`.
- Graph definition:
  ```kotlin
  composable<Route.Onboarding> {
      OnboardingScreen(
          viewModel = onboardingViewModel,
          onScheduleTestAlarm = { title, content ->
              ReminderAlarmScheduler.schedule(
                  context = context,
                  reminderId = 9999L,
                  title = title,
                  content = content,
                  triggerTimeMillis = System.currentTimeMillis() + 1000L
              )
          },
          onOnboardingComplete = {
              preferencesViewModel.markOnboardingCompleted()
              navController.navigate(Route.Home) {
                  popUpTo<Route.Onboarding> { inclusive = true }
              }
          }
      )
  }
  ```
