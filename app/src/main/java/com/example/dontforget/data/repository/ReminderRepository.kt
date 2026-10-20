package com.example.dontforget.data.repository

import com.example.dontforget.data.local.entity.ChecklistItemEntity
import com.example.dontforget.data.local.entity.ReminderEntity
import com.example.dontforget.data.local.model.ReminderWithItems
import kotlinx.coroutines.flow.Flow

interface ReminderRepository {
    fun getActiveReminders(): Flow<List<ReminderWithItems>>
    fun getCompletedReminders(): Flow<List<ReminderWithItems>>
    fun getReminderById(id: Long): Flow<ReminderWithItems?>
    suspend fun getReminderEntityById(id: Long): ReminderEntity?
    suspend fun insertReminder(reminder: ReminderEntity, items: List<ChecklistItemEntity>): Long
    suspend fun updateReminder(reminder: ReminderEntity, items: List<ChecklistItemEntity>)
    suspend fun updateChecklistItemPacked(itemId: Long, isPacked: Boolean)
    suspend fun updateChecklistItems(items: List<ChecklistItemEntity>)
    suspend fun packAllItems(reminderId: Long)
    suspend fun markReminderComplete(reminderId: Long, completedEpochMillis: Long = System.currentTimeMillis())
    suspend fun restoreReminder(reminderId: Long)
    suspend fun deleteReminder(reminderId: Long)
    suspend fun deleteInactiveRemindersOlderThan(cutoffEpochMillis: Long): List<Long>
}
