# Side Effects — Onboarding Feature (`onboarding-chunk`)

## 1. `OnboardingUiSideEffect`
```kotlin
sealed interface OnboardingUiSideEffect : UiSideEffect {
    data class TriggerInstantTestAlarm(
        val reminderId: Long = 9999L,
        val title: String,
        val content: String
    ) : OnboardingUiSideEffect
    
    data object CompleteOnboardingAndNavigateHome : OnboardingUiSideEffect
}
```

## 2. Handling in Composable
- `TriggerInstantTestAlarm`: Calls the abstract schedule lambda argument `onScheduleTestAlarm(effect.title, effect.content)` passed into `OnboardingScreen`.
- `CompleteOnboardingAndNavigateHome`: Calls `preferencesViewModel.markOnboardingCompleted()` and navigates to `Route.Home` clearing `Route.Onboarding` from backstack.
