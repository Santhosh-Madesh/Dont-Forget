# Events — Chunk 03: Template Management (`chunk-03-template-management`)

## 1. Template Management Events
```kotlin
sealed interface TemplateManagementUiEvent : UiEvent {
    data class OnTemplateClick(val templateId: Long) : TemplateManagementUiEvent
    object OnCreateTemplateClick : TemplateManagementUiEvent
}
```

## 2. Template Detail Events
```kotlin
sealed interface TemplateDetailUiEvent : UiEvent {
    object OnEditClick : TemplateDetailUiEvent
    object OnShowDeleteDialogClick : TemplateDetailUiEvent
    object OnDismissDeleteDialogClick : TemplateDetailUiEvent
    object OnConfirmDeleteClick : TemplateDetailUiEvent
    object OnBackClick : TemplateDetailUiEvent
}
```

## 3. Template Edit Events
```kotlin
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
```
