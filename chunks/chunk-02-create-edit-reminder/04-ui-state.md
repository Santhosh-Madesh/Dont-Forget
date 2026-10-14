# UI State Specification — Chunk 02: Create & Edit Reminder Flow

```kotlin
enum class CreateReminderStep {
    SELECT_TEMPLATE,
    CONTEXT,
    CHECKLIST,
    DATE_TIME_NOTIFICATION,
    PREVIEW
}

data class CreateReminderUiState(
    val currentStep: CreateReminderStep = CreateReminderStep.SELECT_TEMPLATE,
    val availableTemplates: List<TemplateWithContextAndItems> = emptyList(),
    val selectedTemplateId: Long? = null,
    val contextTitle: String = "",
    val contextIconName: String = "event",
    val checklistItems: List<String> = emptyList(),
    val departureEpochMillis: Long = System.currentTimeMillis() + 3600000L, // default 1h in future
    val notificationLeadMinutes: Int = 30,
    val isCustomLeadTime: Boolean = false,
    val customLeadMinutesInput: String = "",
    val saveAsTemplate: Boolean = false,
    val templateTitleInput: String = "",
    val validationError: String? = null,
    val showDiscardDialog: Boolean = false,
    val isLoading: Boolean = false
) : UiState

data class EditReminderUiState(
    val reminderId: Long = 0L,
    val contextTitle: String = "",
    val contextIconName: String = "event",
    val checklistItems: List<Pair<Long, String>> = emptyList(), // ID to Text
    val newItemText: String = "",
    val departureEpochMillis: Long = System.currentTimeMillis() + 3600000L,
    val notificationLeadMinutes: Int = 30,
    val isCustomLeadTime: Boolean = false,
    val customLeadMinutesInput: String = "",
    val validationError: String? = null,
    val showDiscardDialog: Boolean = false,
    val isModified: Boolean = false,
    val isLoading: Boolean = true
) : UiState
```
