package com.example.dontforget.feature.templates

import com.example.dontforget.data.local.entity.ChecklistItemEntity
import com.example.dontforget.data.local.entity.ContextEntity
import com.example.dontforget.data.local.entity.TemplateEntity
import com.example.dontforget.data.local.model.TemplateWithContextAndItems
import com.example.dontforget.data.repository.TemplateRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeTemplateRepository : TemplateRepository {
    val contexts = mutableListOf<ContextEntity>()
    val templates = mutableListOf<TemplateEntity>()
    val checklistItems = mutableListOf<ChecklistItemEntity>()

    private val flowTrigger = MutableStateFlow(0)

    private fun notifyChanges() {
        flowTrigger.value = flowTrigger.value + 1
    }

    override fun getAllTemplates(): Flow<List<TemplateWithContextAndItems>> {
        return flowTrigger.map {
            templates.mapNotNull { template ->
                val context = contexts.find { it.id == template.contextId } ?: return@mapNotNull null
                val items = checklistItems.filter { it.parentId == template.id && it.parentType == "TEMPLATE" }
                TemplateWithContextAndItems(template = template, context = context, items = items)
            }
        }
    }

    override fun getTemplateById(id: Long): Flow<TemplateWithContextAndItems?> {
        return flowTrigger.map {
            val template = templates.find { it.id == id } ?: return@map null
            val context = contexts.find { it.id == template.contextId } ?: return@map null
            val items = checklistItems.filter { it.parentId == template.id && it.parentType == "TEMPLATE" }
            TemplateWithContextAndItems(template = template, context = context, items = items)
        }
    }

    override fun getAllContexts(): Flow<List<ContextEntity>> {
        return flowTrigger.map { contexts.toList() }
    }

    override suspend fun getContextByTitle(title: String): ContextEntity? {
        return contexts.find { it.title.equals(title, ignoreCase = true) }
    }

    override suspend fun insertContext(context: ContextEntity): Long {
        val newId = if (context.id == 0L) (contexts.size + 1).toLong() else context.id
        val entity = context.copy(id = newId)
        contexts.add(entity)
        notifyChanges()
        return newId
    }

    override suspend fun insertTemplate(
        template: TemplateEntity,
        items: List<ChecklistItemEntity>
    ): Long {
        val newId = if (template.id == 0L) (templates.size + 1).toLong() else template.id
        val templateEntity = template.copy(id = newId)
        templates.add(templateEntity)
        val itemsWithParent = items.mapIndexed { idx, item ->
            val itemId = if (item.id == 0L) (checklistItems.size + idx + 1).toLong() else item.id
            item.copy(id = itemId, parentId = newId, parentType = "TEMPLATE")
        }
        checklistItems.addAll(itemsWithParent)
        notifyChanges()
        return newId
    }

    override suspend fun updateTemplate(
        template: TemplateEntity,
        items: List<ChecklistItemEntity>
    ) {
        val index = templates.indexOfFirst { it.id == template.id }
        if (index != -1) {
            templates[index] = template
        }
        checklistItems.removeAll { it.parentId == template.id && it.parentType == "TEMPLATE" }
        val itemsWithParent = items.mapIndexed { idx, item ->
            val itemId = if (item.id == 0L) (checklistItems.size + idx + 1).toLong() else item.id
            item.copy(id = itemId, parentId = template.id, parentType = "TEMPLATE")
        }
        checklistItems.addAll(itemsWithParent)
        notifyChanges()
    }

    override suspend fun deleteTemplate(templateId: Long) {
        templates.removeAll { it.id == templateId }
        checklistItems.removeAll { it.parentId == templateId && it.parentType == "TEMPLATE" }
        notifyChanges()
    }
}
