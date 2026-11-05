# Event Specifications — Onboarding Feature (`onboarding-chunk`)

```kotlin
sealed interface OnboardingUiEvent : UiEvent {
    data object OnGetStartedClicked : OnboardingUiEvent
    data object OnSkipClicked : OnboardingUiEvent
    data class OnContextChanged(val context: String) : OnboardingUiEvent
    data class OnNewItemInputChanged(val text: String) : OnboardingUiEvent
    data object OnAddItemClicked : OnboardingUiEvent
    data class OnRemoveItemClicked(val item: String) : OnboardingUiEvent
    data class OnDepartureTimeChanged(val time: String) : OnboardingUiEvent
    data object OnNextClicked : OnboardingUiEvent
    data object OnBackClicked : OnboardingUiEvent
    data object OnConfirmReminderClicked : OnboardingUiEvent
    data class OnToggleItemPacked(val item: String) : OnboardingUiEvent
    data object OnPackAllClicked : OnboardingUiEvent
    data object OnFinishOnboardingClicked : OnboardingUiEvent
}
```
