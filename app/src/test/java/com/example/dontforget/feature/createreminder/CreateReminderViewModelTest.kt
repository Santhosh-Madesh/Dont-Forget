package com.example.dontforget.feature.createreminder

import com.example.dontforget.data.local.entity.ChecklistItemEntity
import com.example.dontforget.data.local.entity.ContextEntity
import com.example.dontforget.data.local.entity.ReminderEntity
import com.example.dontforget.data.local.entity.TemplateEntity
import com.example.dontforget.data.local.model.ReminderWithItems
import com.example.dontforget.data.local.model.TemplateWithContextAndItems
import com.example.dontforget.data.repository.ReminderRepository
import com.example.dontforget.data.repository.TemplateRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FakeTemplateRepository : TemplateRepository {
    val contexts = mutableListOf<ContextEntity>()
    val templates = mutableListOf<TemplateEntity>()
    val items = mutableListOf<ChecklistItemEntity>()
    private val templatesFlow = MutableStateFlow<List<TemplateWithContextAndItems>>(emptyList())

    fun emitTemplates(list: List<TemplateWithContextAndItems>) {
        templatesFlow.value = list
    }

    override fun getAllTemplates(): Flow<List<TemplateWithContextAndItems>> = templatesFlow

    override fun getTemplateById(id: Long): Flow<TemplateWithContextAndItems?> = flow {
        val t = templates.find { it.id == id }
        if (t == null) emit(null)
        else {
            val c = contexts.find { it.id == t.contextId } ?: ContextEntity(1, "College", "school")
            val templateItems = items.filter { it.parentId == t.id && it.parentType == "TEMPLATE" }
            emit(TemplateWithContextAndItems(t, c, templateItems))
        }
    }

    override fun getAllContexts(): Flow<List<ContextEntity>> = flow { emit(contexts) }

    override suspend fun getContextByTitle(title: String): ContextEntity? {
        return contexts.find { it.title.equals(title, ignoreCase = true) }
    }

    override suspend fun insertContext(context: ContextEntity): Long {
        val id = (contexts.size + 1).toLong()
        val newContext = context.copy(id = id)
        contexts.add(newContext)
        return id
    }

    override suspend fun insertTemplate(
        template: TemplateEntity,
        items: List<ChecklistItemEntity>
    ): Long {
        val id = (templates.size + 1).toLong()
        val newTemplate = template.copy(id = id)
        templates.add(newTemplate)
        val templateItems = items.map { it.copy(parentId = id, parentType = "TEMPLATE") }
        this.items.addAll(templateItems)
        return id
    }

    override suspend fun updateTemplate(
        template: TemplateEntity,
        items: List<ChecklistItemEntity>
    ) {
        val index = templates.indexOfFirst { it.id == template.id }
        if (index != -1) {
            templates[index] = template
        }
    }

    override suspend fun deleteTemplate(templateId: Long) {
        templates.removeAll { it.id == templateId }
        items.removeAll { it.parentId == templateId && it.parentType == "TEMPLATE" }
    }
}

class FakeReminderRepository : ReminderRepository {
    val reminders = mutableListOf<ReminderEntity>()
    val items = mutableListOf<ChecklistItemEntity>()

    override fun getActiveReminders(): Flow<List<ReminderWithItems>> = flow { emit(emptyList()) }

    override fun getCompletedReminders(): Flow<List<ReminderWithItems>> = flow { emit(emptyList()) }

    override fun getReminderById(id: Long): Flow<ReminderWithItems?> = flow {
        val rem = reminders.find { it.id == id }
        if (rem == null) {
            emit(null)
        } else {
            val remItems = items.filter { it.parentId == id && it.parentType == "REMINDER" }
            emit(ReminderWithItems(rem, remItems))
        }
    }

    override suspend fun getReminderEntityById(id: Long): ReminderEntity? {
        return reminders.find { it.id == id }
    }

    override suspend fun insertReminder(
        reminder: ReminderEntity,
        items: List<ChecklistItemEntity>
    ): Long {
        val id = (reminders.size + 1).toLong()
        val newRem = reminder.copy(id = id)
        reminders.add(newRem)
        val remItems = items.map { it.copy(parentId = id, parentType = "REMINDER") }
        this.items.addAll(remItems)
        return id
    }

    override suspend fun updateReminder(
        reminder: ReminderEntity,
        items: List<ChecklistItemEntity>
    ) {
        val idx = reminders.indexOfFirst { it.id == reminder.id }
        if (idx != -1) {
            reminders[idx] = reminder
        }
        this.items.removeAll { it.parentId == reminder.id && it.parentType == "REMINDER" }
        val remItems = items.map { it.copy(parentId = reminder.id, parentType = "REMINDER") }
        this.items.addAll(remItems)
    }

    override suspend fun updateChecklistItemPacked(itemId: Long, isPacked: Boolean) {}

    override suspend fun updateChecklistItems(items: List<ChecklistItemEntity>) {}

    override suspend fun packAllItems(reminderId: Long) {}

    override suspend fun markReminderComplete(reminderId: Long, completedEpochMillis: Long) {}

    override suspend fun restoreReminder(reminderId: Long) {}

    override suspend fun deleteReminder(reminderId: Long) {}

    override suspend fun deleteInactiveRemindersOlderThan(cutoffEpochMillis: Long): List<Long> = emptyList()
}

@OptIn(ExperimentalCoroutinesApi::class)
class CreateReminderViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeReminderRepo: FakeReminderRepository
    private lateinit var fakeTemplateRepo: FakeTemplateRepository
    private lateinit var viewModel: CreateReminderViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeReminderRepo = FakeReminderRepository()
        fakeTemplateRepo = FakeTemplateRepository()
        viewModel = CreateReminderViewModel(fakeReminderRepo, fakeTemplateRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial step is SELECT_TEMPLATE`() {
        assertEquals(CreateReminderStep.SELECT_TEMPLATE, viewModel.uiState.value.currentStep)
    }

    @Test
    fun `nextStep from CONTEXT fails validation if title is empty`() {
        viewModel.onEvent(CreateReminderUiEvent.NextStep) // Move to CONTEXT
        assertEquals(CreateReminderStep.CONTEXT, viewModel.uiState.value.currentStep)

        viewModel.onEvent(CreateReminderUiEvent.NextStep) // Attempt move to CHECKLIST
        assertEquals(CreateReminderStep.CONTEXT, viewModel.uiState.value.currentStep)
        assertNotNull(viewModel.uiState.value.validationError)
    }

    @Test
    fun `nextStep from CONTEXT succeeds if title is provided`() {
        viewModel.onEvent(CreateReminderUiEvent.NextStep) // Move to CONTEXT
        viewModel.onEvent(CreateReminderUiEvent.UpdateContextTitle("College"))
        viewModel.onEvent(CreateReminderUiEvent.NextStep) // Move to CHECKLIST

        assertEquals(CreateReminderStep.CHECKLIST, viewModel.uiState.value.currentStep)
        assertNull(viewModel.uiState.value.validationError)
    }

    @Test
    fun `saveReminder creates reminder and emits side effect`() = runTest(testDispatcher) {
        val futureTime = System.currentTimeMillis() + 100000L
        viewModel.onEvent(CreateReminderUiEvent.UpdateContextTitle("College"))
        viewModel.onEvent(CreateReminderUiEvent.SelectContextIcon("school"))
        viewModel.onEvent(CreateReminderUiEvent.AddChecklistItem("ID Card"))
        viewModel.onEvent(CreateReminderUiEvent.SelectDepartureDateTime(futureTime))
        viewModel.onEvent(CreateReminderUiEvent.SelectNotificationLeadMinutes(30))

        viewModel.onEvent(CreateReminderUiEvent.SaveReminder)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(1, fakeReminderRepo.reminders.size)
        assertEquals("College", fakeReminderRepo.reminders[0].contextTitle)
        assertEquals(1, fakeReminderRepo.items.size)
        assertEquals("ID Card", fakeReminderRepo.items[0].text)

        val effect = viewModel.effect.first()
        assertTrue(effect is CreateReminderUiSideEffect.NavigateToReminderDetail)
        assertEquals(1L, (effect as CreateReminderUiSideEffect.NavigateToReminderDetail).reminderId)
    }
}
