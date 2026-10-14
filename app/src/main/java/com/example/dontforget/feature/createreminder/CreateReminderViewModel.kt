package com.example.dontforget.feature.createreminder

import androidx.lifecycle.viewModelScope
import com.example.dontforget.core.mvi.BaseViewModel
import com.example.dontforget.data.local.entity.ChecklistItemEntity
import com.example.dontforget.data.local.entity.ContextEntity
import com.example.dontforget.data.local.entity.ReminderEntity
import com.example.dontforget.data.local.entity.TemplateEntity
import com.example.dontforget.data.repository.ReminderRepository
import com.example.dontforget.data.repository.TemplateRepository
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class CreateReminderViewModel(
    private val reminderRepository: ReminderRepository,
    private val templateRepository: TemplateRepository
) : BaseViewModel<CreateReminderUiState, CreateReminderUiEvent, CreateReminderUiSideEffect>(
    initialState = CreateReminderUiState()
) {

    init {
        observeTemplates()
    }

    private fun observeTemplates() {
        templateRepository.getAllTemplates()
            .onEach { templates ->
                updateState { copy(availableTemplates = templates) }
            }
            .launchIn(viewModelScope)
    }

    override fun onEvent(event: CreateReminderUiEvent) {
        when (event) {
            is CreateReminderUiEvent.SelectStep -> {
                updateState { copy(currentStep = event.step, validationError = null) }
            }
            CreateReminderUiEvent.NextStep -> {
                validateAndAdvanceStep()
            }
            CreateReminderUiEvent.PreviousStep -> {
                val current = currentState.currentStep
                if (current == CreateReminderStep.SELECT_TEMPLATE) {
                    updateState { copy(showDiscardDialog = true) }
                } else {
                    val prevStepIndex = current.ordinal - 1
                    if (prevStepIndex >= 0) {
                        updateState {
                            copy(
                                currentStep = CreateReminderStep.entries[prevStepIndex],
                                validationError = null
                            )
                        }
                    }
                }
            }
            is CreateReminderUiEvent.SelectTemplate -> {
                val template = currentState.availableTemplates.find { it.template.id == event.templateId }
                if (template != null) {
                    updateState {
                        copy(
                            selectedTemplateId = template.template.id,
                            contextTitle = template.context.title,
                            contextIconName = template.context.iconName,
                            checklistItems = template.items.sortedBy { it.displayOrder }.map { it.text },
                            templateTitleInput = template.template.title,
                            currentStep = CreateReminderStep.CONTEXT,
                            validationError = null
                        )
                    }
                }
            }
            CreateReminderUiEvent.SkipTemplate -> {
                updateState {
                    copy(
                        selectedTemplateId = null,
                        contextTitle = "",
                        contextIconName = "event",
                        checklistItems = emptyList(),
                        currentStep = CreateReminderStep.CONTEXT,
                        validationError = null
                    )
                }
            }
            is CreateReminderUiEvent.UpdateContextTitle -> {
                updateState { copy(contextTitle = event.title, validationError = null) }
            }
            is CreateReminderUiEvent.SelectContextIcon -> {
                updateState { copy(contextIconName = event.iconName) }
            }
            is CreateReminderUiEvent.AddChecklistItem -> {
                val trimmed = event.itemText.trim()
                if (trimmed.isNotEmpty()) {
                    updateState {
                        copy(
                            checklistItems = checklistItems + trimmed,
                            validationError = null
                        )
                    }
                }
            }
            is CreateReminderUiEvent.EditChecklistItem -> {
                val list = currentState.checklistItems.toMutableList()
                if (event.index in list.indices) {
                    list[event.index] = event.newText
                    updateState { copy(checklistItems = list) }
                }
            }
            is CreateReminderUiEvent.RemoveChecklistItem -> {
                val list = currentState.checklistItems.toMutableList()
                if (event.index in list.indices) {
                    list.removeAt(event.index)
                    updateState { copy(checklistItems = list) }
                }
            }
            is CreateReminderUiEvent.ReorderChecklistItem -> {
                val list = currentState.checklistItems.toMutableList()
                if (event.fromIndex in list.indices && event.toIndex in list.indices) {
                    val item = list.removeAt(event.fromIndex)
                    list.add(event.toIndex, item)
                    updateState { copy(checklistItems = list) }
                }
            }
            is CreateReminderUiEvent.SelectDepartureDateTime -> {
                updateState { copy(departureEpochMillis = event.epochMillis, validationError = null) }
            }
            is CreateReminderUiEvent.SelectNotificationLeadMinutes -> {
                updateState {
                    copy(
                        notificationLeadMinutes = event.minutes,
                        isCustomLeadTime = false,
                        validationError = null
                    )
                }
            }
            is CreateReminderUiEvent.UpdateCustomLeadMinutes -> {
                val minutes = event.input.toIntOrNull() ?: 30
                updateState {
                    copy(
                        customLeadMinutesInput = event.input,
                        notificationLeadMinutes = minutes,
                        isCustomLeadTime = true,
                        validationError = null
                    )
                }
            }
            is CreateReminderUiEvent.ToggleSaveAsTemplate -> {
                updateState { copy(saveAsTemplate = event.enabled) }
            }
            is CreateReminderUiEvent.UpdateTemplateTitle -> {
                updateState { copy(templateTitleInput = event.title) }
            }
            CreateReminderUiEvent.SaveReminder -> {
                saveReminderInternal()
            }
            CreateReminderUiEvent.RequestDiscard -> {
                updateState { copy(showDiscardDialog = true) }
            }
            CreateReminderUiEvent.ConfirmDiscard -> {
                updateState { copy(showDiscardDialog = false) }
                sendEffect(CreateReminderUiSideEffect.NavigateHome)
            }
            CreateReminderUiEvent.DismissDiscardDialog -> {
                updateState { copy(showDiscardDialog = false) }
            }
        }
    }

    private fun validateAndAdvanceStep() {
        val state = currentState
        when (state.currentStep) {
            CreateReminderStep.SELECT_TEMPLATE -> {
                updateState { copy(currentStep = CreateReminderStep.CONTEXT, validationError = null) }
            }
            CreateReminderStep.CONTEXT -> {
                if (state.contextTitle.isBlank()) {
                    updateState { copy(validationError = "Context title cannot be empty") }
                    return
                }
                updateState { copy(currentStep = CreateReminderStep.CHECKLIST, validationError = null) }
            }
            CreateReminderStep.CHECKLIST -> {
                if (state.checklistItems.isEmpty()) {
                    updateState { copy(validationError = "Please add at least one checklist item") }
                    return
                }
                updateState { copy(currentStep = CreateReminderStep.DATE_TIME_NOTIFICATION, validationError = null) }
            }
            CreateReminderStep.DATE_TIME_NOTIFICATION -> {
                if (state.departureEpochMillis <= System.currentTimeMillis()) {
                    updateState { copy(validationError = "Departure time cannot be in the past") }
                    return
                }
                if (state.notificationLeadMinutes <= 0) {
                    updateState { copy(validationError = "Notification lead time must be greater than 0") }
                    return
                }
                updateState { copy(currentStep = CreateReminderStep.PREVIEW, validationError = null) }
            }
            CreateReminderStep.PREVIEW -> {
                saveReminderInternal()
            }
        }
    }

    private fun saveReminderInternal() {
        val state = currentState

        if (state.contextTitle.isBlank()) {
            updateState { copy(validationError = "Context title cannot be empty") }
            return
        }
        if (state.checklistItems.isEmpty()) {
            updateState { copy(validationError = "Please add at least one checklist item") }
            return
        }
        if (state.departureEpochMillis <= System.currentTimeMillis()) {
            updateState { copy(validationError = "Departure time cannot be in the past") }
            return
        }

        viewModelScope.launch {
            updateState { copy(isLoading = true) }

            // Ensure Context exists in database
            val contextEntity = templateRepository.getContextByTitle(state.contextTitle)
            val contextId = if (contextEntity != null) {
                contextEntity.id
            } else {
                templateRepository.insertContext(
                    ContextEntity(title = state.contextTitle, iconName = state.contextIconName)
                )
            }

            // Create Reminder Entity
            val reminder = ReminderEntity(
                contextId = contextId,
                contextTitle = state.contextTitle,
                contextIconName = state.contextIconName,
                departureEpochMillis = state.departureEpochMillis,
                notificationLeadMinutes = state.notificationLeadMinutes,
                isCompleted = false,
                createdEpochMillis = System.currentTimeMillis()
            )

            val checklistEntities = state.checklistItems.mapIndexed { index, itemText ->
                ChecklistItemEntity(
                    parentId = 0L,
                    parentType = "REMINDER",
                    text = itemText,
                    isPacked = false,
                    displayOrder = index
                )
            }

            val newReminderId = reminderRepository.insertReminder(reminder, checklistEntities)

            // Save as Template if toggled
            if (state.saveAsTemplate) {
                val templateTitle = state.templateTitleInput.ifBlank { state.contextTitle }
                val templateEntity = TemplateEntity(
                    contextId = contextId,
                    title = templateTitle
                )
                val templateItems = state.checklistItems.mapIndexed { index, itemText ->
                    ChecklistItemEntity(
                        parentId = 0L,
                        parentType = "TEMPLATE",
                        text = itemText,
                        isPacked = false,
                        displayOrder = index
                    )
                }
                templateRepository.insertTemplate(templateEntity, templateItems)
            }

            updateState { copy(isLoading = false) }
            sendEffect(CreateReminderUiSideEffect.NavigateToReminderDetail(newReminderId))
        }
    }
}
