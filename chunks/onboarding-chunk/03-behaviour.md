# Behaviour Matrix — Onboarding Feature (`onboarding-chunk`)

## 1. Triggers & Behaviour Rules

| Trigger Event | Preconditions | Expected Outcome / State Transition | AC Reference |
| :--- | :--- | :--- | :--- |
| `OnGetStartedClicked` | Step is `WELCOME` | Transition `currentStep` to `CHOOSE_CONTEXT`. | AC-ONB-4 |
| `OnSkipClicked` | Step is `WELCOME` | Emit `CompleteOnboardingAndNavigateHome` side effect. `PreferencesViewModel` updates `ONBOARDING_COMPLETED = true` in DataStore and navigates to `Route.Home`. | AC-ONB-5 |
| `OnContextChanged(text)` | Step is `CHOOSE_CONTEXT` | Update `contextText = text`. | AC-ONB-6 |
| `OnNewItemInputChanged(text)`| Step is `ADD_ITEMS` | Update `newItemInput = text`. | AC-ONB-7 |
| `OnAddItemClicked` | Step is `ADD_ITEMS`, `newItemInput` not blank | Append `newItemInput` to `checklistItems` list, reset `newItemInput = ""`. | AC-ONB-7 |
| `OnRemoveItemClicked(item)`| Step is `ADD_ITEMS` | Remove `item` from `checklistItems`. | AC-ONB-7 |
| `OnDepartureTimeChanged(time)`| Step is `CONFIGURE_TIMING` | Update `departureTimeText = time`. | AC-ONB-8 |
| `OnNextClicked` | Step is `CHOOSE_CONTEXT` | Transition `currentStep` to `ADD_ITEMS`. | AC-ONB-6 |
| `OnNextClicked` | Step is `ADD_ITEMS` | Transition `currentStep` to `CONFIGURE_TIMING`. | AC-ONB-7 |
| `OnNextClicked` | Step is `CONFIGURE_TIMING` | Transition `currentStep` to `CONFIRMATION_SUMMARY`. | AC-ONB-8 |
| `OnNextClicked` | Step is `INTERACTIVE_PACKING` | Transition `currentStep` to `COMPLETION_VICTORY`. | AC-ONB-11 |
| `OnBackClicked` | Step > `WELCOME` | Move `currentStep` to previous step enum in order. | AC-ONB-3 |
| `OnConfirmReminderClicked` | Step is `CONFIRMATION_SUMMARY` | Emit `TriggerInstantTestAlarm(title, content)` side effect (invoking `onScheduleTestAlarm` parameter function), and transition `currentStep` to `INTERACTIVE_PACKING`. | AC-ONB-10 |
| `OnToggleItemPacked(item)`| Step is `INTERACTIVE_PACKING` | If `item` in `packedItems`, remove it; otherwise add to `packedItems`. | AC-ONB-11 |
| `OnPackAllClicked` | Step is `INTERACTIVE_PACKING` | If all items packed, clear `packedItems`; otherwise set `packedItems = checklistItems.toSet()`. | AC-ONB-11 |
| `OnFinishOnboardingClicked`| Step is `COMPLETION_VICTORY` | Emit `CompleteOnboardingAndNavigateHome` side effect. `PreferencesViewModel` updates `ONBOARDING_COMPLETED = true` in DataStore and navigates to `Route.Home`. | AC-ONB-13 |
