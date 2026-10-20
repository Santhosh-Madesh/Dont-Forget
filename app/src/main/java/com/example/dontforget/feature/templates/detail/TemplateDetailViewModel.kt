package com.example.dontforget.feature.templates.detail

import androidx.lifecycle.viewModelScope
import com.example.dontforget.core.mvi.BaseViewModel
import com.example.dontforget.data.repository.TemplateRepository
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class TemplateDetailViewModel(
    private val templateId: Long,
    private val templateRepository: TemplateRepository
) : BaseViewModel<TemplateDetailUiState, TemplateDetailUiEvent, TemplateDetailUiSideEffect>(
    initialState = TemplateDetailUiState()
) {

    init {
        observeTemplate()
    }

    private fun observeTemplate() {
        templateRepository.getTemplateById(templateId)
            .onEach { templateWithItems ->
                updateState {
                    copy(
                        isLoading = false,
                        templateWithContextAndItems = templateWithItems
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    override fun onEvent(event: TemplateDetailUiEvent) {
        when (event) {
            TemplateDetailUiEvent.OnEditClick -> {
                sendEffect(TemplateDetailUiSideEffect.NavigateToEdit(templateId))
            }
            TemplateDetailUiEvent.OnShowDeleteDialogClick -> {
                updateState { copy(showDeleteDialog = true) }
            }
            TemplateDetailUiEvent.OnDismissDeleteDialogClick -> {
                updateState { copy(showDeleteDialog = false) }
            }
            TemplateDetailUiEvent.OnConfirmDeleteClick -> {
                updateState { copy(showDeleteDialog = false) }
                viewModelScope.launch {
                    templateRepository.deleteTemplate(templateId)
                    sendEffect(TemplateDetailUiSideEffect.NavigateBack)
                }
            }
            TemplateDetailUiEvent.OnBackClick -> {
                sendEffect(TemplateDetailUiSideEffect.NavigateBack)
            }
        }
    }
}
