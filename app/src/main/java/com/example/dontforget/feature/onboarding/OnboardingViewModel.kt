package com.example.dontforget.feature.onboarding

import com.example.dontforget.core.mvi.BaseViewModel

class OnboardingViewModel : BaseViewModel<OnboardingUiState, OnboardingUiEvent, OnboardingUiSideEffect>(
    initialState = OnboardingUiState()
) {

    override fun onEvent(event: OnboardingUiEvent) {
        when (event) {
            OnboardingUiEvent.OnGetStartedClicked -> {
                updateState { copy(currentStep = OnboardingStep.CHOOSE_CONTEXT) }
            }
            OnboardingUiEvent.OnSkipClicked -> {
                sendEffect(OnboardingUiSideEffect.CompleteOnboardingAndNavigateHome)
            }
            is OnboardingUiEvent.OnContextChanged -> {
                updateState { copy(contextText = event.context) }
            }
            is OnboardingUiEvent.OnNewItemInputChanged -> {
                updateState { copy(newItemInput = event.text) }
            }
            OnboardingUiEvent.OnAddItemClicked -> {
                if (currentState.newItemInput.isNotBlank()) {
                    val updatedItems = currentState.checklistItems + currentState.newItemInput.trim()
                    updateState { copy(checklistItems = updatedItems, newItemInput = "") }
                }
            }
            is OnboardingUiEvent.OnRemoveItemClicked -> {
                val updatedItems = currentState.checklistItems.filterNot { it == event.item }
                updateState { copy(checklistItems = updatedItems) }
            }
            is OnboardingUiEvent.OnDepartureTimeChanged -> {
                updateState { copy(departureTimeText = event.time) }
            }
            OnboardingUiEvent.OnNextClicked -> {
                val nextStep = when (currentState.currentStep) {
                    OnboardingStep.WELCOME -> OnboardingStep.CHOOSE_CONTEXT
                    OnboardingStep.CHOOSE_CONTEXT -> OnboardingStep.ADD_ITEMS
                    OnboardingStep.ADD_ITEMS -> OnboardingStep.CONFIGURE_TIMING
                    OnboardingStep.CONFIGURE_TIMING -> OnboardingStep.CONFIRMATION_SUMMARY
                    OnboardingStep.CONFIRMATION_SUMMARY -> OnboardingStep.INTERACTIVE_PACKING
                    OnboardingStep.INTERACTIVE_PACKING -> OnboardingStep.COMPLETION_VICTORY
                    OnboardingStep.COMPLETION_VICTORY -> OnboardingStep.COMPLETION_VICTORY
                }
                updateState { copy(currentStep = nextStep) }
            }
            OnboardingUiEvent.OnBackClicked -> {
                val prevStep = when (currentState.currentStep) {
                    OnboardingStep.WELCOME -> OnboardingStep.WELCOME
                    OnboardingStep.CHOOSE_CONTEXT -> OnboardingStep.WELCOME
                    OnboardingStep.ADD_ITEMS -> OnboardingStep.CHOOSE_CONTEXT
                    OnboardingStep.CONFIGURE_TIMING -> OnboardingStep.ADD_ITEMS
                    OnboardingStep.CONFIRMATION_SUMMARY -> OnboardingStep.CONFIGURE_TIMING
                    OnboardingStep.INTERACTIVE_PACKING -> OnboardingStep.CONFIRMATION_SUMMARY
                    OnboardingStep.COMPLETION_VICTORY -> OnboardingStep.INTERACTIVE_PACKING
                }
                updateState { copy(currentStep = prevStep) }
            }
            OnboardingUiEvent.OnConfirmReminderClicked -> {
                val itemsList = if (currentState.checklistItems.isNotEmpty()) {
                    currentState.checklistItems.joinToString(", ")
                } else "Don't forget your essential items"

                sendEffect(
                    OnboardingUiSideEffect.TriggerInstantTestAlarm(
                        reminderId = 9999L,
                        title = "${currentState.contextText} Departure",
                        content = "Don't forget: $itemsList"
                    )
                )
                updateState { copy(currentStep = OnboardingStep.INTERACTIVE_PACKING) }
            }
            is OnboardingUiEvent.OnToggleItemPacked -> {
                val currentPacked = currentState.packedItems
                val updatedPacked = if (currentPacked.contains(event.item)) {
                    currentPacked - event.item
                } else {
                    currentPacked + event.item
                }
                updateState { copy(packedItems = updatedPacked) }
            }
            OnboardingUiEvent.OnPackAllClicked -> {
                val allItems = currentState.checklistItems.toSet()
                val updatedPacked = if (currentState.packedItems.containsAll(allItems)) {
                    emptySet()
                } else {
                    allItems
                }
                updateState { copy(packedItems = updatedPacked) }
            }
            OnboardingUiEvent.OnFinishOnboardingClicked -> {
                sendEffect(OnboardingUiSideEffect.CompleteOnboardingAndNavigateHome)
            }
        }
    }
}
