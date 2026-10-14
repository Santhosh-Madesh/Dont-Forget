package com.example.dontforget.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.dontforget.data.local.entity.ChecklistItemEntity

@Dao
interface ChecklistItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<ChecklistItemEntity>)

    @Update
    suspend fun updateItem(item: ChecklistItemEntity)

    @Query("UPDATE checklist_items SET isPacked = :isPacked WHERE id = :itemId")
    suspend fun updatePackedStatus(itemId: Long, isPacked: Boolean)

    @Query("UPDATE checklist_items SET isPacked = 1 WHERE parentId = :parentId AND parentType = 'REMINDER'")
    suspend fun packAllForReminder(parentId: Long)

    @Query("DELETE FROM checklist_items WHERE parentId = :parentId AND parentType = :parentType")
    suspend fun deleteItemsForParent(parentId: Long, parentType: String)

    @Query("DELETE FROM checklist_items WHERE parentId IN (:parentIds) AND parentType = 'REMINDER'")
    suspend fun deleteItemsForReminders(parentIds: List<Long>)
}
