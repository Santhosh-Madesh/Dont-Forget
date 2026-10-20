package com.example.dontforget.feature.templates.edit

import com.example.dontforget.core.mvi.UiEvent
import com.example.dontforget.core.mvi.UiSideEffect
import com.example.dontforget.core.mvi.UiState
import com.example.dontforget.data.local.entity.ContextEntity

data class TemplateEditUiState(
    val templateId: Long = 0L,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val availableContexts: List<ContextEntity> = emptyList(),
    val selectedContextId: Long? = null,
    val isNewContext: Boolean = false,
    val contextTitleInput: String = "",
    val contextIconName: String = "event",
    val templateTitleInput: String = "",
    val checklistItems: List<String> = emptyList(),
    val newItemInput: String = "",
    val validationError: String? = null,
    val showDiscardDialog: Boolean = false
) : UiState {
    val isEditing: Boolean
        get() = templateId > 0L
}

sealed interface TemplateEditUiEvent : UiEvent {
    data class OnTemplateTitleChange(val title: String) : TemplateEditUiEvent
    data class OnSelectExistingContext(val contextId: Long) : TemplateEditUiEvent
    data class OnToggleNewContext(val isNew: Boolean) : TemplateEditUiEvent
    data class OnContextTitleChange(val title: String) : TemplateEditUiEvent
    data class OnContextIconSelect(val iconName: String) : TemplateEditUiEvent
    data class OnNewItemInputChange(val input: String) : TemplateEditUiEvent
    object OnAddChecklistItem : TemplateEditUiEvent
    data class OnEditChecklistItem(val index: Int, val newText: String) : TemplateEditUiEvent
    data class OnRemoveChecklistItem(val index: Int) : TemplateEditUiEvent
    data class OnReorderChecklistItem(val fromIndex: Int, val toIndex: Int) : TemplateEditUiEvent
    object OnSaveTemplateClick : TemplateEditUiEvent
    object OnBackClick : TemplateEditUiEvent
    object OnConfirmDiscardClick : TemplateEditUiEvent
    object OnDismissDiscardDialogClick : TemplateEditUiEvent
}

sealed interface TemplateEditUiSideEffect : UiSideEffect {
    object NavigateBack : TemplateEditUiSideEffect
    data class ShowSnackbar(val message: String) : TemplateEditUiSideEffect
}
