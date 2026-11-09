package com.example.dontforget.feature.onboarding

import com.example.dontforget.core.mvi.BaseViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class OnboardingViewModel : BaseViewModel<OnboardingUiState, OnboardingUiEvent, OnboardingUiSideEffect>(
    initialState = OnboardingUiState(
        departureTimeText = calculateDepartureTime()
    )
) {

    companion object {
        private fun calculateDepartureTime(): String {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.MINUTE, 30)
            val sdf = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
            return sdf.format(calendar.time)
        }
    }

    override fun onEvent(event: OnboardingUiEvent) {
        when (event) {
            OnboardingUiEvent.OnGetStartedClicked -> {
                updateState { copy(currentStep = OnboardingStep.CHOOSE_CONTEXT, isError = false, errorMsg = null) }
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
                    updateState { copy(checklistItems = updatedItems, newItemInput = "", isError = false, errorMsg = null) }
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
                when (currentState.currentStep) {
                    OnboardingStep.WELCOME -> {
                        updateState { copy(currentStep = OnboardingStep.CHOOSE_CONTEXT, isError = false, errorMsg = null) }
                    }
                    OnboardingStep.CHOOSE_CONTEXT -> {
                        if(currentState.contextText.isEmpty()) {
                            updateState {
                                copy(isError = true, errorMsg = "Please set the context name")
                            }
                        } else {
                            updateState { copy(currentStep = OnboardingStep.ADD_ITEMS, isError = false, errorMsg = null) }
                        }

                    }
                    OnboardingStep.ADD_ITEMS -> {
                        if (currentState.checklistItems.isEmpty()) {
                            updateState { copy(isError = true, errorMsg = "Please add at least one item before continuing!") }
                        } else {
                            updateState { copy(currentStep = OnboardingStep.CONFIGURE_TIMING, isError = false, errorMsg = null) }
                        }
                    }
                    OnboardingStep.CONFIGURE_TIMING -> {
                        updateState { copy(currentStep = OnboardingStep.CONFIRMATION_SUMMARY, isError = false, errorMsg = null) }
                    }
                    OnboardingStep.CONFIRMATION_SUMMARY -> {
                        updateState { copy(currentStep = OnboardingStep.INTERACTIVE_PACKING, isReminderPopupVisible = true, isError = false, errorMsg = null) }
                    }
                    OnboardingStep.INTERACTIVE_PACKING -> {
                        val allItemsPacked = currentState.checklistItems.isNotEmpty() &&
                                currentState.packedItems.containsAll(currentState.checklistItems)
                        if (!allItemsPacked) {
                            updateState { copy(isError = true, errorMsg = "Hold up! Check off and pack all your items before finishing up!") }
                        } else {
                            updateState { copy(currentStep = OnboardingStep.COMPLETION_VICTORY, isError = false, errorMsg = null) }
                        }
                    }
                    OnboardingStep.COMPLETION_VICTORY -> {
                        updateState { copy(currentStep = OnboardingStep.COMPLETION_VICTORY, isError = false, errorMsg = null) }
                    }
                }
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
                updateState { copy(currentStep = prevStep, isError = false, errorMsg = null) }
            }
            OnboardingUiEvent.OnConfirmReminderClicked -> {
                updateState { copy(currentStep = OnboardingStep.INTERACTIVE_PACKING, isReminderPopupVisible = true, isError = false, errorMsg = null) }
            }
            OnboardingUiEvent.OnDismissReminderPopup -> {
                updateState { copy(isReminderPopupVisible = false) }
            }
            is OnboardingUiEvent.OnToggleItemPacked -> {
                val currentPacked = currentState.packedItems
                val updatedPacked = if (currentPacked.contains(event.item)) {
                    currentPacked - event.item
                } else {
                    currentPacked + event.item
                }
                updateState { copy(packedItems = updatedPacked, isError = false, errorMsg = null) }
            }
            OnboardingUiEvent.OnPackAllClicked -> {
                val allItems = currentState.checklistItems.toSet()
                val updatedPacked = if (currentState.packedItems.containsAll(allItems)) {
                    emptySet()
                } else {
                    allItems
                }
                updateState { copy(packedItems = updatedPacked, isError = false, errorMsg = null) }
            }
            OnboardingUiEvent.OnFinishOnboardingClicked -> {
                sendEffect(OnboardingUiSideEffect.CompleteOnboardingAndNavigateHome)
            }
        }
    }
}
