# Test Plan — Onboarding Feature (`onboarding-chunk`)

## 1. Traceability Table

| Acceptance Criterion | Test Class | Test Case Name |
| :--- | :--- | :--- |
| AC-ONB-1 | `PreferencesRepositoryTest` | `setOnboardingCompleted_persistsToDataStore` |
| AC-ONB-2 | `PreferencesViewModelTest` | `initialState_reflectsDataStoreValue` |
| AC-ONB-3 | `OnboardingViewModelTest` | `initialState_defaultStepIsWelcome` |
| AC-ONB-4 | `OnboardingViewModelTest` | `onGetStartedClicked_transitionsToChooseContextStep` |
| AC-ONB-5 | `OnboardingViewModelTest` | `onSkipClicked_emitsCompleteOnboardingSideEffect` |
| AC-ONB-6 | `OnboardingViewModelTest` | `onContextChanged_updatesContextText` |
| AC-ONB-7 | `OnboardingViewModelTest` | `onAddItemClicked_appendsItemToList` |
| AC-ONB-8 | `OnboardingViewModelTest` | `onDepartureTimeChanged_updatesTimeText` |
| AC-ONB-9 | `OnboardingViewModelTest` | `onConfirmReminderClicked_emitsTriggerInstantTestAlarm` |
| AC-ONB-10 | `OnboardingViewModelTest` | `onToggleItemPacked_updatesPackedSet` |
| AC-ONB-11 | `OnboardingViewModelTest` | `onFinishOnboardingClicked_emitsCompleteOnboardingSideEffect` |
