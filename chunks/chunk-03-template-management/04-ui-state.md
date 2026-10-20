# UI State — Chunk 03: Template Management (`chunk-03-template-management`)

## 1. Template Management State
```kotlin
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
```

## 2. Template Detail State
```kotlin
data class TemplateDetailUiState(
    val isLoading: Boolean = true,
    val templateWithContextAndItems: TemplateWithContextAndItems? = null,
    val showDeleteDialog: Boolean = false
) : UiState
```

## 3. Template Edit State
```kotlin
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
```
