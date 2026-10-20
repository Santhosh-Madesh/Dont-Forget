# Side Effects — Chunk 03: Template Management (`chunk-03-template-management`)

## 1. Template Management Side Effects
```kotlin
sealed interface TemplateManagementUiSideEffect : UiSideEffect {
    data class NavigateToDetail(val templateId: Long) : TemplateManagementUiSideEffect
    object NavigateToCreate : TemplateManagementUiSideEffect
}
```

## 2. Template Detail Side Effects
```kotlin
sealed interface TemplateDetailUiSideEffect : UiSideEffect {
    data class NavigateToEdit(val templateId: Long) : TemplateDetailUiSideEffect
    object NavigateBack : TemplateDetailUiSideEffect
    data class ShowSnackbar(val message: String) : TemplateDetailUiSideEffect
}
```

## 3. Template Edit Side Effects
```kotlin
sealed interface TemplateEditUiSideEffect : UiSideEffect {
    object NavigateBack : TemplateEditUiSideEffect
    data class ShowSnackbar(val message: String) : TemplateEditUiSideEffect
}
```
