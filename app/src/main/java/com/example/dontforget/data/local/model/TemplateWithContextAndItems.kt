package com.example.dontforget.data.local.model

import androidx.room.Embedded
import androidx.room.Relation
import com.example.dontforget.data.local.entity.ChecklistItemEntity
import com.example.dontforget.data.local.entity.ContextEntity
import com.example.dontforget.data.local.entity.TemplateEntity

data class TemplateWithContextAndItems(
    @Embedded val template: TemplateEntity,
    @Relation(
        parentColumn = "contextId",
        entityColumn = "id"
    )
    val context: ContextEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "parentId"
    )
    val items: List<ChecklistItemEntity>
) {
    val itemsFiltered: List<ChecklistItemEntity>
        get() = items.filter { it.parentType == "TEMPLATE" }.sortedBy { it.displayOrder }
}
