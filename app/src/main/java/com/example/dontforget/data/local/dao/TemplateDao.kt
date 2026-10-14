package com.example.dontforget.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.dontforget.data.local.entity.TemplateEntity
import com.example.dontforget.data.local.model.TemplateWithContextAndItems
import kotlinx.coroutines.flow.Flow

@Dao
interface TemplateDao {
    @Transaction
    @Query("SELECT * FROM templates")
    fun getAllTemplates(): Flow<List<TemplateWithContextAndItems>>

    @Transaction
    @Query("SELECT * FROM templates WHERE id = :id")
    fun getTemplateById(id: Long): Flow<TemplateWithContextAndItems?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplate(template: TemplateEntity): Long

    @Update
    suspend fun updateTemplate(template: TemplateEntity)

    @Delete
    suspend fun deleteTemplate(template: TemplateEntity)

    @Query("DELETE FROM templates WHERE id = :id")
    suspend fun deleteTemplateById(id: Long)
}
