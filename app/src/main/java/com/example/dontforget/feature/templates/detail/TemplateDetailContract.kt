package com.example.dontforget.feature.templates.detail

import com.example.dontforget.core.mvi.UiEvent
import com.example.dontforget.core.mvi.UiSideEffect
import com.example.dontforget.core.mvi.UiState
import com.example.dontforget.data.local.model.TemplateWithContextAndItems

data class TemplateDetailUiState(
    val isLoading: Boolean = true,
    val templateWithContextAndItems: TemplateWithContextAndItems? = null,
    val showDeleteDialog: Boolean = false
) : UiState

sealed interface TemplateDetailUiEvent : UiEvent {
    object OnEditClick : TemplateDetailUiEvent
    object OnShowDeleteDialogClick : TemplateDetailUiEvent
    object OnDismissDeleteDialogClick : TemplateDetailUiEvent
    object OnConfirmDeleteClick : TemplateDetailUiEvent
    object OnBackClick : TemplateDetailUiEvent
}

sealed interface TemplateDetailUiSideEffect : UiSideEffect {
    data class NavigateToEdit(val templateId: Long) : TemplateDetailUiSideEffect
    object NavigateBack : TemplateDetailUiSideEffect
    data class ShowSnackbar(val message: String) : TemplateDetailUiSideEffect
}
