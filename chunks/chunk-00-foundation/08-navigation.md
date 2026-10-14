# Navigation — Chunk 00: Shared Foundation (`chunk-00-foundation`)

## 1. Routes Definition (`com.example.dontforget.core.navigation.Route`)

```kotlin
package com.example.dontforget.core.navigation

sealed class Route(val route: String) {
    object Home : Route("home")
    object CreateReminder : Route("create_reminder")
    data class EditReminder(val reminderId: Long) : Route("edit_reminder/{reminderId}") {
        companion object {
            const val ROUTE = "edit_reminder/{reminderId}"
            fun createRoute(reminderId: Long) = "edit_reminder/$reminderId"
        }
    }
    object TemplateManagement : Route("template_management")
    data class TemplateDetail(val templateId: Long) : Route("template_detail/{templateId}") {
        companion object {
            const val ROUTE = "template_detail/{templateId}"
            fun createRoute(templateId: Long) = "template_detail/$templateId"
        }
    }
    data class TemplateEdit(val templateId: Long) : Route("template_edit/{templateId}") {
        companion object {
            const val ROUTE = "template_edit/{templateId}"
            fun createRoute(templateId: Long) = "template_edit/$templateId"
        }
    }
    data class ReminderDetail(val reminderId: Long) : Route("reminder_detail/{reminderId}") {
        companion object {
            const val ROUTE = "reminder_detail/{reminderId}"
            fun createRoute(reminderId: Long) = "reminder_detail/$reminderId"
        }
    }
}
```
