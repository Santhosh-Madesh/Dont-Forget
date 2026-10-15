package com.example.dontforget.data.repository

import com.example.dontforget.data.local.dao.ChecklistItemDao
import com.example.dontforget.data.local.dao.ReminderDao
import com.example.dontforget.data.local.entity.ChecklistItemEntity
import com.example.dontforget.data.local.entity.ReminderEntity
import com.example.dontforget.data.local.model.ReminderWithItems
import kotlinx.coroutines.flow.Flow

class ReminderRepositoryImpl(
    private val reminderDao: ReminderDao,
    private val checklistItemDao: ChecklistItemDao
) : ReminderRepository {

    override fun getActiveReminders(): Flow<List<ReminderWithItems>> {
        return reminderDao.getActiveReminders()
    }

    override fun getCompletedReminders(): Flow<List<ReminderWithItems>> {
        return reminderDao.getCompletedReminders()
    }

    override fun getReminderById(id: Long): Flow<ReminderWithItems?> {
        return reminderDao.getReminderById(id)
    }

    override suspend fun getReminderEntityById(id: Long): ReminderEntity? {
        return reminderDao.getReminderEntityById(id)
    }

    override suspend fun insertReminder(
        reminder: ReminderEntity,
        items: List<ChecklistItemEntity>
    ): Long {
        val reminderId = reminderDao.insertReminder(reminder)
        val itemsWithParent = items.map {
            it.copy(parentId = reminderId, parentType = "REMINDER")
        }
        checklistItemDao.insertItems(itemsWithParent)
        return reminderId
    }

    override suspend fun updateReminder(
        reminder: ReminderEntity,
        items: List<ChecklistItemEntity>
    ) {
        reminderDao.updateReminder(reminder)
        checklistItemDao.deleteItemsForParent(reminder.id, "REMINDER")
        val itemsWithParent = items.map {
            it.copy(parentId = reminder.id, parentType = "REMINDER")
        }
        checklistItemDao.insertItems(itemsWithParent)
    }

    override suspend fun updateChecklistItemPacked(itemId: Long, isPacked: Boolean) {
        checklistItemDao.updatePackedStatus(itemId, isPacked)
    }

    override suspend fun packAllItems(reminderId: Long) {
        checklistItemDao.packAllForReminder(reminderId)
    }

    override suspend fun markReminderComplete(reminderId: Long, completedEpochMillis: Long) {
        reminderDao.markCompleted(reminderId, completedEpochMillis)
    }

    override suspend fun restoreReminder(reminderId: Long) {
        reminderDao.restoreReminder(reminderId)
    }

    override suspend fun deleteReminder(reminderId: Long) {
        checklistItemDao.deleteItemsForParent(reminderId, "REMINDER")
        reminderDao.deleteReminderById(reminderId)
    }

    override suspend fun deleteInactiveRemindersOlderThan(cutoffEpochMillis: Long): List<Long> {
        val expiredIds = reminderDao.getInactiveReminderIdsOlderThan(cutoffEpochMillis)
        if (expiredIds.isNotEmpty()) {
            checklistItemDao.deleteItemsForReminders(expiredIds)
            reminderDao.deleteInactiveRemindersOlderThan(cutoffEpochMillis)
        }
        return expiredIds
    }
}
