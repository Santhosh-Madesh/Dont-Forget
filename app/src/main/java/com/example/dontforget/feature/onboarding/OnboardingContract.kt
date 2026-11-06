package com.example.dontforget.feature.onboarding

import com.example.dontforget.core.mvi.UiEvent
import com.example.dontforget.core.mvi.UiSideEffect
import com.example.dontforget.core.mvi.UiState

enum class OnboardingStep {
    WELCOME,
    CHOOSE_CONTEXT,
    ADD_ITEMS,
    CONFIGURE_TIMING,
    CONFIRMATION_SUMMARY,
    INTERACTIVE_PACKING,
    COMPLETION_VICTORY
}

data class OnboardingUiState(
    val currentStep: OnboardingStep = OnboardingStep.WELCOME,
    val contextText: String = "Gym",
    val checklistItems: List<String> = listOf("Water bottle", "Towel", "Headphones"),
    val departureTimeText: String = "06:00 PM",
    val reminderLeadText: String = "5:30 PM (-30m)",
    val packedItems: Set<String> = emptySet(),
    val newItemInput: String = ""
) : UiState

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

sealed interface OnboardingUiSideEffect : UiSideEffect {
    data class TriggerInstantTestAlarm(
        val reminderId: Long = 9999L,
        val title: String,
        val content: String
    ) : OnboardingUiSideEffect

    data object CompleteOnboardingAndNavigateHome : OnboardingUiSideEffect
}
