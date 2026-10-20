package com.example.dontforget.feature.templates

import com.example.dontforget.data.local.entity.ChecklistItemEntity
import com.example.dontforget.data.local.entity.ContextEntity
import com.example.dontforget.data.local.entity.TemplateEntity
import com.example.dontforget.feature.templates.edit.TemplateEditUiEvent
import com.example.dontforget.feature.templates.edit.TemplateEditUiSideEffect
import com.example.dontforget.feature.templates.edit.TemplateEditViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TemplateEditViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeTemplateRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeTemplateRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `validation prevents saving with empty title or items`() = runTest(testDispatcher) {
        val viewModel = TemplateEditViewModel(0L, repository)

        // Attempt save with empty title
        viewModel.onEvent(TemplateEditUiEvent.OnSaveTemplateClick)
        assertEquals("Template title cannot be blank", viewModel.uiState.value.validationError)

        // Add title but empty context
        viewModel.onEvent(TemplateEditUiEvent.OnTemplateTitleChange("My Template"))
        viewModel.onEvent(TemplateEditUiEvent.OnToggleNewContext(true))
        viewModel.onEvent(TemplateEditUiEvent.OnContextTitleChange(""))
        viewModel.onEvent(TemplateEditUiEvent.OnSaveTemplateClick)
        assertEquals("Context name cannot be blank", viewModel.uiState.value.validationError)

        // Add context but no items
        viewModel.onEvent(TemplateEditUiEvent.OnContextTitleChange("College"))
        viewModel.onEvent(TemplateEditUiEvent.OnSaveTemplateClick)
        assertEquals("Add at least one checklist item", viewModel.uiState.value.validationError)
    }

    @Test
    fun `creates new template and navigates back`() = runTest(testDispatcher) {
        val viewModel = TemplateEditViewModel(0L, repository)

        viewModel.onEvent(TemplateEditUiEvent.OnTemplateTitleChange("Exam Prep"))
        viewModel.onEvent(TemplateEditUiEvent.OnToggleNewContext(true))
        viewModel.onEvent(TemplateEditUiEvent.OnContextTitleChange("College"))
        viewModel.onEvent(TemplateEditUiEvent.OnContextIconSelect("school"))

        viewModel.onEvent(TemplateEditUiEvent.OnNewItemInputChange("Pen & Paper"))
        viewModel.onEvent(TemplateEditUiEvent.OnAddChecklistItem)

        viewModel.onEvent(TemplateEditUiEvent.OnSaveTemplateClick)
        testDispatcher.scheduler.advanceUntilIdle()

        val effect = viewModel.effect.first()
        assertEquals(TemplateEditUiSideEffect.NavigateBack, effect)

        assertEquals(1, repository.templates.size)
        assertEquals("Exam Prep", repository.templates[0].title)
        assertEquals(1, repository.checklistItems.size)
        assertEquals("Pen & Paper", repository.checklistItems[0].text)
    }

    @Test
    fun `edits existing template and updates repository`() = runTest(testDispatcher) {
        val context = ContextEntity(id = 1L, title = "College", iconName = "school")
        repository.contexts.add(context)
        val template = TemplateEntity(id = 5L, contextId = 1L, title = "Old Title")
        val items = listOf(ChecklistItemEntity(id = 1L, parentId = 5L, parentType = "TEMPLATE", text = "Old Item", isPacked = false, displayOrder = 0))
        repository.insertTemplate(template, items)

        val viewModel = TemplateEditViewModel(5L, repository)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("Old Title", viewModel.uiState.value.templateTitleInput)

        viewModel.onEvent(TemplateEditUiEvent.OnTemplateTitleChange("Updated Title"))
        viewModel.onEvent(TemplateEditUiEvent.OnEditChecklistItem(0, "Updated Item"))
        viewModel.onEvent(TemplateEditUiEvent.OnSaveTemplateClick)

        testDispatcher.scheduler.advanceUntilIdle()
        val effect = viewModel.effect.first()
        assertEquals(TemplateEditUiSideEffect.NavigateBack, effect)

        val savedTemplate = repository.templates.find { it.id == 5L }
        assertNotNull(savedTemplate)
        assertEquals("Updated Title", savedTemplate?.title)
        val savedItems = repository.checklistItems.filter { it.parentId == 5L }
        assertEquals(1, savedItems.size)
        assertEquals("Updated Item", savedItems[0].text)
    }
}
