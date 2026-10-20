package com.example.dontforget.data.repository

import android.content.Context
import com.example.dontforget.data.local.dao.ChecklistItemDao
import com.example.dontforget.data.local.dao.ReminderDao
import com.example.dontforget.data.local.entity.ChecklistItemEntity
import com.example.dontforget.data.local.entity.ReminderEntity
import com.example.dontforget.data.local.model.ReminderWithItems
import com.example.dontforget.notification.ReminderAlarmScheduler
import com.example.dontforget.notification.ReminderNotificationManager
import kotlinx.coroutines.flow.Flow

class ReminderRepositoryImpl(
    private val reminderDao: ReminderDao,
    private val checklistItemDao: ChecklistItemDao,
    private val context: Context? = null
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

        context?.let { ctx ->
            if (!reminder.isCompleted) {
                ReminderAlarmScheduler.scheduleForReminder(
                    context = ctx,
                    reminderId = reminderId,
                    title = reminder.contextTitle,
                    departureEpochMillis = reminder.departureEpochMillis,
                    leadMinutes = reminder.notificationLeadMinutes
                )
            }
        }
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

        context?.let { ctx ->
            ReminderAlarmScheduler.cancel(ctx, reminder.id)
            if (!reminder.isCompleted) {
                ReminderAlarmScheduler.scheduleForReminder(
                    context = ctx,
                    reminderId = reminder.id,
                    title = reminder.contextTitle,
                    departureEpochMillis = reminder.departureEpochMillis,
                    leadMinutes = reminder.notificationLeadMinutes
                )
            }
        }
    }

    override suspend fun updateChecklistItemPacked(itemId: Long, isPacked: Boolean) {
        checklistItemDao.updatePackedStatus(itemId, isPacked)
    }

    override suspend fun updateChecklistItems(items: List<ChecklistItemEntity>) {
        checklistItemDao.updateItems(items)
    }

    override suspend fun packAllItems(reminderId: Long) {
        checklistItemDao.packAllForReminder(reminderId)
    }

    override suspend fun markReminderComplete(reminderId: Long, completedEpochMillis: Long) {
        reminderDao.markCompleted(reminderId, completedEpochMillis)
        context?.let { ctx ->
            ReminderAlarmScheduler.cancel(ctx, reminderId)
            ReminderNotificationManager.cancelNotification(ctx, reminderId)
        }
    }

    override suspend fun restoreReminder(reminderId: Long) {
        reminderDao.restoreReminder(reminderId)
        context?.let { ctx ->
            val reminder = reminderDao.getReminderEntityById(reminderId)
            if (reminder != null) {
                ReminderAlarmScheduler.scheduleForReminder(
                    context = ctx,
                    reminderId = reminder.id,
                    title = reminder.contextTitle,
                    departureEpochMillis = reminder.departureEpochMillis,
                    leadMinutes = reminder.notificationLeadMinutes
                )
            }
        }
    }

    override suspend fun deleteReminder(reminderId: Long) {
        context?.let { ctx ->
            ReminderAlarmScheduler.cancel(ctx, reminderId)
            ReminderNotificationManager.cancelNotification(ctx, reminderId)
        }
        checklistItemDao.deleteItemsForParent(reminderId, "REMINDER")
        reminderDao.deleteReminderById(reminderId)
    }

    override suspend fun deleteInactiveRemindersOlderThan(cutoffEpochMillis: Long): List<Long> {
        val expiredIds = reminderDao.getInactiveReminderIdsOlderThan(cutoffEpochMillis)
        if (expiredIds.isNotEmpty()) {
            context?.let { ctx ->
                expiredIds.forEach { id ->
                    ReminderAlarmScheduler.cancel(ctx, id)
                    ReminderNotificationManager.cancelNotification(ctx, id)
                }
            }
            checklistItemDao.deleteItemsForReminders(expiredIds)
            reminderDao.deleteInactiveRemindersOlderThan(cutoffEpochMillis)
        }
        return expiredIds
    }
}
