package com.example.dontforget.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.dontforget.data.local.entity.ReminderEntity
import com.example.dontforget.data.local.model.ReminderWithItems
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {
    @Transaction
    @Query("SELECT * FROM reminders WHERE isCompleted = 0 ORDER BY createdEpochMillis DESC")
    fun getActiveReminders(): Flow<List<ReminderWithItems>>

    @Transaction
    @Query("SELECT * FROM reminders WHERE id = :id")
    fun getReminderById(id: Long): Flow<ReminderWithItems?>

    @Query("SELECT * FROM reminders WHERE id = :id")
    suspend fun getReminderEntityById(id: Long): ReminderEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: ReminderEntity): Long

    @Update
    suspend fun updateReminder(reminder: ReminderEntity)

    @Query("UPDATE reminders SET isCompleted = 1, completedEpochMillis = :completedTime WHERE id = :id")
    suspend fun markCompleted(id: Long, completedTime: Long)

    @Query("UPDATE reminders SET isCompleted = 0, completedEpochMillis = NULL WHERE id = :id")
    suspend fun restoreReminder(id: Long)

    @Query("DELETE FROM reminders WHERE id = :id")
    suspend fun deleteReminderById(id: Long)

    @Query("SELECT id FROM reminders WHERE isCompleted = 1 AND completedEpochMillis IS NOT NULL AND completedEpochMillis <= :cutoffTime")
    suspend fun getInactiveReminderIdsOlderThan(cutoffTime: Long): List<Long>

    @Query("DELETE FROM reminders WHERE isCompleted = 1 AND completedEpochMillis IS NOT NULL AND completedEpochMillis <= :cutoffTime")
    suspend fun deleteInactiveRemindersOlderThan(cutoffTime: Long)
}
