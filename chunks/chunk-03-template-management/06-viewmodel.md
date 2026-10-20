# ViewModel — Chunk 03: Template Management (`chunk-03-template-management`)

## 1. TemplateManagementViewModel Specification
```kotlin
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
                    .map { (context, list) -> ContextTemplateGroup(context, list) }
                    .sortedBy { it.context.title }
                updateState { copy(isLoading = false, groupedTemplates = grouped) }
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
```

## 2. TemplateDetailViewModel Specification
```kotlin
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
                updateState { copy(isLoading = false, templateWithContextAndItems = templateWithItems) }
            }
            .launchIn(viewModelScope)
    }

    override fun onEvent(event: TemplateDetailUiEvent) {
        when (event) {
            TemplateDetailUiEvent.OnEditClick -> sendEffect(TemplateDetailUiSideEffect.NavigateToEdit(templateId))
            TemplateDetailUiEvent.OnShowDeleteDialogClick -> updateState { copy(showDeleteDialog = true) }
            TemplateDetailUiEvent.OnDismissDeleteDialogClick -> updateState { copy(showDeleteDialog = false) }
            TemplateDetailUiEvent.OnConfirmDeleteClick -> {
                updateState { copy(showDeleteDialog = false) }
                viewModelScope.launch {
                    templateRepository.deleteTemplate(templateId)
                    sendEffect(TemplateDetailUiSideEffect.NavigateBack)
                }
            }
            TemplateDetailUiEvent.OnBackClick -> sendEffect(TemplateDetailUiSideEffect.NavigateBack)
        }
    }
}
```

## 3. TemplateEditViewModel Specification
```kotlin
class TemplateEditViewModel(
    private val templateId: Long,
    private val templateRepository: TemplateRepository
) : BaseViewModel<TemplateEditUiState, TemplateEditUiEvent, TemplateEditUiSideEffect>(
    initialState = TemplateEditUiState(templateId = templateId)
) {
    // Manages context fetching, template loading, checklist editing, field validation, and template persistence.
}
```
