package com.example.dontforget.data.repository

import com.example.dontforget.data.local.dao.ContextDao
import com.example.dontforget.data.local.dao.TemplateDao
import com.example.dontforget.data.local.entity.ChecklistItemEntity
import com.example.dontforget.data.local.entity.ContextEntity
import com.example.dontforget.data.local.entity.TemplateEntity
import com.example.dontforget.data.local.model.TemplateWithContextAndItems
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class FakeContextDao : ContextDao {
    val contexts = mutableListOf<ContextEntity>()

    override fun getAllContexts(): Flow<List<ContextEntity>> = error("Not needed for unit test")
    override suspend fun getContextById(id: Long) = contexts.find { it.id == id }

    override suspend fun getContextByTitle(title: String): ContextEntity? {
        return contexts.find { it.title.equals(title, ignoreCase = true) }
    }

    override suspend fun insertContext(context: ContextEntity): Long {
        val id = if (context.id == 0L) (contexts.size + 1).toLong() else context.id
        val newContext = context.copy(id = id)
        contexts.add(newContext)
        return id
    }

    override suspend fun updateContext(context: ContextEntity) {
        val index = contexts.indexOfFirst { it.id == context.id }
        if (index != -1) {
            contexts[index] = context
        }
    }

    override suspend fun deleteContext(context: ContextEntity) {
        contexts.remove(context)
    }
}

class FakeTemplateDao : TemplateDao {
    val templates = mutableListOf<TemplateEntity>()

    override fun getAllTemplates(): Flow<List<TemplateWithContextAndItems>> = error("Not needed for unit test")
    override fun getTemplateById(id: Long): Flow<TemplateWithContextAndItems?> = error("Not needed for unit test")

    override suspend fun insertTemplate(template: TemplateEntity): Long {
        val id = if (template.id == 0L) (templates.size + 1).toLong() else template.id
        val newTemplate = template.copy(id = id)
        templates.add(newTemplate)
        return id
    }

    override suspend fun updateTemplate(template: TemplateEntity) {
        val index = templates.indexOfFirst { it.id == template.id }
        if (index != -1) {
            templates[index] = template
        }
    }

    override suspend fun deleteTemplate(template: TemplateEntity) {
        templates.remove(template)
    }

    override suspend fun deleteTemplateById(id: Long) {
        templates.removeAll { it.id == id }
    }
}

class TemplateRepositoryTest {

    private lateinit var fakeContextDao: FakeContextDao
    private lateinit var fakeTemplateDao: FakeTemplateDao
    private lateinit var fakeChecklistItemDao: FakeChecklistItemDao
    private lateinit var repository: TemplateRepositoryImpl

    @Before
    fun setUp() {
        fakeContextDao = FakeContextDao()
        fakeTemplateDao = FakeTemplateDao()
        fakeChecklistItemDao = FakeChecklistItemDao()
        repository = TemplateRepositoryImpl(fakeContextDao, fakeTemplateDao, fakeChecklistItemDao)
    }

    @Test
    fun `insertContext saves context successfully`() = runTest {
        val context = ContextEntity(title = "College", iconName = "school")
        val id = repository.insertContext(context)

        assertEquals(1L, id)
        assertEquals(1, fakeContextDao.contexts.size)
        assertEquals("College", fakeContextDao.contexts[0].title)
    }

    @Test
    fun `insertTemplate saves template and checklist items`() = runTest {
        val template = TemplateEntity(contextId = 1, title = "Daily College Packing")
        val items = listOf<ChecklistItemEntity>()

        val id = repository.insertTemplate(template, items)

        assertEquals(1L, id)
        assertEquals(1, fakeTemplateDao.templates.size)
    }
}
