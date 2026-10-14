package com.example.dontforget.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "checklist_items")
data class ChecklistItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val parentId: Long,
    val parentType: String, // "REMINDER" or "TEMPLATE"
    val text: String,
    val isPacked: Boolean = false,
    val displayOrder: Int = 0
)
