package com.example.dontforget.data.repository

import com.example.dontforget.data.local.entity.ChecklistItemEntity
import com.example.dontforget.data.local.entity.ContextEntity
import com.example.dontforget.data.local.entity.TemplateEntity
import com.example.dontforget.data.local.model.TemplateWithContextAndItems
import kotlinx.coroutines.flow.Flow

interface TemplateRepository {
    fun getAllTemplates(): Flow<List<TemplateWithContextAndItems>>
    fun getTemplateById(id: Long): Flow<TemplateWithContextAndItems?>
    fun getAllContexts(): Flow<List<ContextEntity>>
    suspend fun getContextByTitle(title: String): ContextEntity?
    suspend fun insertContext(context: ContextEntity): Long
    suspend fun insertTemplate(template: TemplateEntity, items: List<ChecklistItemEntity>): Long
    suspend fun updateTemplate(template: TemplateEntity, items: List<ChecklistItemEntity>)
    suspend fun deleteTemplate(templateId: Long)
}
