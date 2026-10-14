package com.example.dontforget.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.dontforget.data.local.dao.ChecklistItemDao
import com.example.dontforget.data.local.dao.ContextDao
import com.example.dontforget.data.local.dao.ReminderDao
import com.example.dontforget.data.local.dao.TemplateDao
import com.example.dontforget.data.local.entity.ChecklistItemEntity
import com.example.dontforget.data.local.entity.ContextEntity
import com.example.dontforget.data.local.entity.ReminderEntity
import com.example.dontforget.data.local.entity.TemplateEntity

@Database(
    entities = [
        ContextEntity::class,
        TemplateEntity::class,
        ReminderEntity::class,
        ChecklistItemEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class DontForgetDatabase : RoomDatabase() {

    abstract fun contextDao(): ContextDao
    abstract fun templateDao(): TemplateDao
    abstract fun reminderDao(): ReminderDao
    abstract fun checklistItemDao(): ChecklistItemDao

    companion object {
        @Volatile
        private var INSTANCE: DontForgetDatabase? = null

        fun getDatabase(context: Context): DontForgetDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DontForgetDatabase::class.java,
                    "dont_forget_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
