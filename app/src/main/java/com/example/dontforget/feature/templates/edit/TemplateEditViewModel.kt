package com.example.dontforget.feature.templates.edit

import androidx.lifecycle.viewModelScope
import com.example.dontforget.core.mvi.BaseViewModel
import com.example.dontforget.data.local.entity.ChecklistItemEntity
import com.example.dontforget.data.local.entity.ContextEntity
import com.example.dontforget.data.local.entity.TemplateEntity
import com.example.dontforget.data.repository.TemplateRepository
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class TemplateEditViewModel(
    private val templateId: Long,
    private val templateRepository: TemplateRepository
) : BaseViewModel<TemplateEditUiState, TemplateEditUiEvent, TemplateEditUiSideEffect>(
    initialState = TemplateEditUiState(templateId = templateId)
) {

    init {
        observeContexts()
        if (templateId > 0L) {
            loadTemplate()
        }
    }

    private fun observeContexts() {
        templateRepository.getAllContexts()
            .onEach { contexts ->
                updateState {
                    val defaultContextId = selectedContextId ?: contexts.firstOrNull()?.id
                    copy(
                        availableContexts = contexts,
                        selectedContextId = defaultContextId
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun loadTemplate() {
        updateState { copy(isLoading = true) }
        viewModelScope.launch {
            val templateData = templateRepository.getTemplateById(templateId).firstOrNull()
            if (templateData != null) {
                updateState {
                    copy(
                        isLoading = false,
                        selectedContextId = templateData.context.id,
                        contextTitleInput = templateData.context.title,
                        contextIconName = templateData.context.iconName,
                        templateTitleInput = templateData.template.title,
                        checklistItems = templateData.items.sortedBy { it.displayOrder }.map { it.text }
                    )
                }
            } else {
                updateState { copy(isLoading = false) }
            }
        }
    }

    override fun onEvent(event: TemplateEditUiEvent) {
        when (event) {
            is TemplateEditUiEvent.OnTemplateTitleChange -> {
                updateState { copy(templateTitleInput = event.title, validationError = null) }
            }
            is TemplateEditUiEvent.OnSelectExistingContext -> {
                val context = currentState.availableContexts.find { it.id == event.contextId }
                updateState {
                    copy(
                        selectedContextId = event.contextId,
                        isNewContext = false,
                        contextTitleInput = context?.title ?: "",
                        contextIconName = context?.iconName ?: "event",
                        validationError = null
                    )
                }
            }
            is TemplateEditUiEvent.OnToggleNewContext -> {
                updateState {
                    copy(
                        isNewContext = event.isNew,
                        contextTitleInput = if (event.isNew) "" else (availableContexts.find { it.id == selectedContextId }?.title ?: ""),
                        validationError = null
                    )
                }
            }
            is TemplateEditUiEvent.OnContextTitleChange -> {
                updateState { copy(contextTitleInput = event.title, validationError = null) }
            }
            is TemplateEditUiEvent.OnContextIconSelect -> {
                updateState { copy(contextIconName = event.iconName) }
            }
            is TemplateEditUiEvent.OnNewItemInputChange -> {
                updateState { copy(newItemInput = event.input) }
            }
            TemplateEditUiEvent.OnAddChecklistItem -> {
                val text = currentState.newItemInput.trim()
                if (text.isNotEmpty()) {
                    updateState {
                        copy(
                            checklistItems = checklistItems + text,
                            newItemInput = "",
                            validationError = null
                        )
                    }
                }
            }
            is TemplateEditUiEvent.OnEditChecklistItem -> {
                val list = currentState.checklistItems.toMutableList()
                if (event.index in list.indices) {
                    list[event.index] = event.newText
                    updateState { copy(checklistItems = list) }
                }
            }
            is TemplateEditUiEvent.OnRemoveChecklistItem -> {
                val list = currentState.checklistItems.toMutableList()
                if (event.index in list.indices) {
                    list.removeAt(event.index)
                    updateState { copy(checklistItems = list) }
                }
            }
            is TemplateEditUiEvent.OnReorderChecklistItem -> {
                val list = currentState.checklistItems.toMutableList()
                if (event.fromIndex in list.indices && event.toIndex in list.indices) {
                    val item = list.removeAt(event.fromIndex)
                    list.add(event.toIndex, item)
                    updateState { copy(checklistItems = list) }
                }
            }
            TemplateEditUiEvent.OnSaveTemplateClick -> {
                saveTemplate()
            }
            TemplateEditUiEvent.OnBackClick -> {
                updateState { copy(showDiscardDialog = true) }
            }
            TemplateEditUiEvent.OnConfirmDiscardClick -> {
                updateState { copy(showDiscardDialog = false) }
                sendEffect(TemplateEditUiSideEffect.NavigateBack)
            }
            TemplateEditUiEvent.OnDismissDiscardDialogClick -> {
                updateState { copy(showDiscardDialog = false) }
            }
        }
    }

    private fun saveTemplate() {
        val state = currentState

        val contextTitle = if (state.isNewContext) {
            state.contextTitleInput.trim()
        } else {
            state.availableContexts.find { it.id == state.selectedContextId }?.title ?: state.contextTitleInput.trim()
        }

        if (state.templateTitleInput.isBlank()) {
            updateState { copy(validationError = "Template title cannot be blank") }
            return
        }
        if (contextTitle.isBlank()) {
            updateState { copy(validationError = "Context name cannot be blank") }
            return
        }
        if (state.checklistItems.isEmpty()) {
            updateState { copy(validationError = "Add at least one checklist item") }
            return
        }

        viewModelScope.launch {
            updateState { copy(isSaving = true) }

            val contextId = if (state.isNewContext || state.selectedContextId == null) {
                val existing = templateRepository.getContextByTitle(contextTitle)
                existing?.id ?: templateRepository.insertContext(
                    ContextEntity(title = contextTitle, iconName = state.contextIconName)
                )
            } else {
                state.selectedContextId
            }

            val templateEntity = TemplateEntity(
                id = if (state.isEditing) state.templateId else 0L,
                contextId = contextId,
                title = state.templateTitleInput.trim()
            )

            val checklistEntities = state.checklistItems.mapIndexed { index, itemText ->
                ChecklistItemEntity(
                    parentId = if (state.isEditing) state.templateId else 0L,
                    parentType = "TEMPLATE",
                    text = itemText,
                    isPacked = false,
                    displayOrder = index
                )
            }

            if (state.isEditing) {
                templateRepository.updateTemplate(templateEntity, checklistEntities)
            } else {
                templateRepository.insertTemplate(templateEntity, checklistEntities)
            }

            updateState { copy(isSaving = false) }
            sendEffect(TemplateEditUiSideEffect.NavigateBack)
        }
    }
}
