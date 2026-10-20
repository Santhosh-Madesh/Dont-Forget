package com.example.dontforget.feature.templates.management

import com.example.dontforget.core.mvi.UiEvent
import com.example.dontforget.core.mvi.UiSideEffect
import com.example.dontforget.core.mvi.UiState
import com.example.dontforget.data.local.entity.ContextEntity
import com.example.dontforget.data.local.model.TemplateWithContextAndItems

data class ContextTemplateGroup(
    val context: ContextEntity,
    val templates: List<TemplateWithContextAndItems>
)

data class TemplateManagementUiState(
    val isLoading: Boolean = true,
    val groupedTemplates: List<ContextTemplateGroup> = emptyList()
) : UiState {
    val isEmpty: Boolean
        get() = !isLoading && groupedTemplates.all { it.templates.isEmpty() }
}

sealed interface TemplateManagementUiEvent : UiEvent {
    data class OnTemplateClick(val templateId: Long) : TemplateManagementUiEvent
    object OnCreateTemplateClick : TemplateManagementUiEvent
}

sealed interface TemplateManagementUiSideEffect : UiSideEffect {
    data class NavigateToDetail(val templateId: Long) : TemplateManagementUiSideEffect
    object NavigateToCreate : TemplateManagementUiSideEffect
}
