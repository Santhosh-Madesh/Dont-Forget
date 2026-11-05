# UI State Model — Onboarding Feature (`onboarding-chunk`)

## 1. `OnboardingStep` Enum
```kotlin
enum class OnboardingStep {
    WELCOME,
    CHOOSE_CONTEXT,
    ADD_ITEMS,
    CONFIGURE_TIMING,
    CONFIRMATION_SUMMARY,
    INTERACTIVE_PACKING,
    COMPLETION_VICTORY
}
```

## 2. `OnboardingUiState`
```kotlin
data class OnboardingUiState(
    val currentStep: OnboardingStep = OnboardingStep.WELCOME,
    val contextText: String = "Gym",
    val checklistItems: List<String> = listOf("Water bottle", "Towel", "Headphones"),
    val departureTimeText: String = "06:00 PM",
    val reminderLeadText: String = "5:30 PM (-30m)",
    val packedItems: Set<String> = emptySet(),
    val newItemInput: String = ""
) : UiState
```
