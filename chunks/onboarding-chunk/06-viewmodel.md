# ViewModel Architecture — Onboarding Feature (`onboarding-chunk`)

## 1. `PreferencesViewModel`
App startup controller checking preferences and controlling startup route:
- Constructor Dependencies: `PreferencesRepository`
- Public State: `val isOnboardingCompleted: StateFlow<Boolean?>` (null while loading DataStore)
- Functions: `fun markOnboardingCompleted()`

## 2. `OnboardingViewModel`
Onboarding step and UI logic reducer:
- Base class: `BaseViewModel<OnboardingUiState, OnboardingUiEvent, OnboardingUiSideEffect>`
- Decoupled from `PreferencesRepository`.
- Event handling maps events directly to `_uiState.update { ... }` or `sendSideEffect(...)`.
