package com.example.dontforget.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val contextId: Long,
    val contextTitle: String,
    val contextIconName: String,
    val departureEpochMillis: Long,
    val notificationLeadMinutes: Int = 30,
    val isCompleted: Boolean = false,
    val createdEpochMillis: Long = System.currentTimeMillis(),
    val completedEpochMillis: Long? = null
)
