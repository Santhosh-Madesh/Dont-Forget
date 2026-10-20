package com.example.dontforget.feature.templates.management

import androidx.lifecycle.viewModelScope
import com.example.dontforget.core.mvi.BaseViewModel
import com.example.dontforget.data.repository.TemplateRepository
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class TemplateManagementViewModel(
    private val templateRepository: TemplateRepository
) : BaseViewModel<TemplateManagementUiState, TemplateManagementUiEvent, TemplateManagementUiSideEffect>(
    initialState = TemplateManagementUiState()
) {

    init {
        observeTemplates()
    }

    private fun observeTemplates() {
        templateRepository.getAllTemplates()
            .onEach { templates ->
                val grouped = templates.groupBy { it.context }
                    .map { (context, templateList) ->
                        ContextTemplateGroup(context, templateList)
                    }
                    .sortedBy { it.context.title }
                updateState {
                    copy(
                        isLoading = false,
                        groupedTemplates = grouped
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    override fun onEvent(event: TemplateManagementUiEvent) {
        when (event) {
            is TemplateManagementUiEvent.OnTemplateClick -> {
                sendEffect(TemplateManagementUiSideEffect.NavigateToDetail(event.templateId))
            }
            TemplateManagementUiEvent.OnCreateTemplateClick -> {
                sendEffect(TemplateManagementUiSideEffect.NavigateToCreate)
            }
        }
    }
}
