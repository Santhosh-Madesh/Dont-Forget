package com.example.dontforget.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.dontforget.data.local.entity.ContextEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ContextDao {
    @Query("SELECT * FROM contexts ORDER BY title ASC")
    fun getAllContexts(): Flow<List<ContextEntity>>

    @Query("SELECT * FROM contexts WHERE id = :id")
    suspend fun getContextById(id: Long): ContextEntity?

    @Query("SELECT * FROM contexts WHERE LOWER(title) = LOWER(:title) LIMIT 1")
    suspend fun getContextByTitle(title: String): ContextEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContext(context: ContextEntity): Long

    @Update
    suspend fun updateContext(context: ContextEntity)

    @Delete
    suspend fun deleteContext(context: ContextEntity)
}
