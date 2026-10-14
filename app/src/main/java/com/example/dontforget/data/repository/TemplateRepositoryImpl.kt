package com.example.dontforget.data.repository

import com.example.dontforget.data.local.dao.ChecklistItemDao
import com.example.dontforget.data.local.dao.ContextDao
import com.example.dontforget.data.local.dao.TemplateDao
import com.example.dontforget.data.local.entity.ChecklistItemEntity
import com.example.dontforget.data.local.entity.ContextEntity
import com.example.dontforget.data.local.entity.TemplateEntity
import com.example.dontforget.data.local.model.TemplateWithContextAndItems
import kotlinx.coroutines.flow.Flow

class TemplateRepositoryImpl(
    private val contextDao: ContextDao,
    private val templateDao: TemplateDao,
    private val checklistItemDao: ChecklistItemDao
) : TemplateRepository {

    override fun getAllTemplates(): Flow<List<TemplateWithContextAndItems>> {
        return templateDao.getAllTemplates()
    }

    override fun getTemplateById(id: Long): Flow<TemplateWithContextAndItems?> {
        return templateDao.getTemplateById(id)
    }

    override fun getAllContexts(): Flow<List<ContextEntity>> {
        return contextDao.getAllContexts()
    }

    override suspend fun getContextByTitle(title: String): ContextEntity? {
        return contextDao.getContextByTitle(title)
    }

    override suspend fun insertContext(context: ContextEntity): Long {
        return contextDao.insertContext(context)
    }

    override suspend fun insertTemplate(
        template: TemplateEntity,
        items: List<ChecklistItemEntity>
    ): Long {
        val templateId = templateDao.insertTemplate(template)
        val itemsWithParent = items.map {
            it.copy(parentId = templateId, parentType = "TEMPLATE")
        }
        checklistItemDao.insertItems(itemsWithParent)
        return templateId
    }

    override suspend fun updateTemplate(
        template: TemplateEntity,
        items: List<ChecklistItemEntity>
    ) {
        templateDao.updateTemplate(template)
        checklistItemDao.deleteItemsForParent(template.id, "TEMPLATE")
        val itemsWithParent = items.map {
            it.copy(parentId = template.id, parentType = "TEMPLATE")
        }
        checklistItemDao.insertItems(itemsWithParent)
    }

    override suspend fun deleteTemplate(templateId: Long) {
        checklistItemDao.deleteItemsForParent(templateId, "TEMPLATE")
        templateDao.deleteTemplateById(templateId)
    }
}
