package com.example.dontforget.data.local.model

import androidx.room.Embedded
import androidx.room.Relation
import com.example.dontforget.data.local.entity.ChecklistItemEntity
import com.example.dontforget.data.local.entity.ReminderEntity

data class ReminderWithItems(
    @Embedded val reminder: ReminderEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "parentId"
    )
    val items: List<ChecklistItemEntity>
) {
    val itemsFiltered: List<ChecklistItemEntity>
        get() = items.filter { it.parentType == "REMINDER" }.sortedBy { it.displayOrder }
}
