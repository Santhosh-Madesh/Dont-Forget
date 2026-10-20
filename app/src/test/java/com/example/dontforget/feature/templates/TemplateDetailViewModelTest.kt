package com.example.dontforget.feature.templates

import com.example.dontforget.data.local.entity.ChecklistItemEntity
import com.example.dontforget.data.local.entity.ContextEntity
import com.example.dontforget.data.local.entity.TemplateEntity
import com.example.dontforget.feature.templates.detail.TemplateDetailUiEvent
import com.example.dontforget.feature.templates.detail.TemplateDetailUiSideEffect
import com.example.dontforget.feature.templates.detail.TemplateDetailViewModel
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
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TemplateDetailViewModelTest {

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
    fun `loads template details successfully`() = runTest(testDispatcher) {
        val context = ContextEntity(id = 1L, title = "College", iconName = "school")
        repository.contexts.add(context)
        val template = TemplateEntity(id = 10L, contextId = 1L, title = "Lab Checklist")
        val items = listOf(ChecklistItemEntity(id = 1L, parentId = 10L, parentType = "TEMPLATE", text = "Apron", isPacked = false, displayOrder = 0))
        repository.insertTemplate(template, items)

        val viewModel = TemplateDetailViewModel(10L, repository)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals("Lab Checklist", state.templateWithContextAndItems?.template?.title)
        assertEquals("College", state.templateWithContextAndItems?.context?.title)
        assertEquals(1, state.templateWithContextAndItems?.items?.size)
    }

    @Test
    fun `onEditClick emits NavigateToEdit effect`() = runTest(testDispatcher) {
        val viewModel = TemplateDetailViewModel(10L, repository)
        viewModel.onEvent(TemplateDetailUiEvent.OnEditClick)

        val effect = viewModel.effect.first()
        assertEquals(TemplateDetailUiSideEffect.NavigateToEdit(10L), effect)
    }

    @Test
    fun `delete template dialog flow deletes template and navigates back`() = runTest(testDispatcher) {
        val context = ContextEntity(id = 1L, title = "College", iconName = "school")
        repository.contexts.add(context)
        val template = TemplateEntity(id = 10L, contextId = 1L, title = "Lab Checklist")
        repository.insertTemplate(template, emptyList())

        val viewModel = TemplateDetailViewModel(10L, repository)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onEvent(TemplateDetailUiEvent.OnShowDeleteDialogClick)
        assertTrue(viewModel.uiState.value.showDeleteDialog)

        viewModel.onEvent(TemplateDetailUiEvent.OnDismissDeleteDialogClick)
        assertFalse(viewModel.uiState.value.showDeleteDialog)

        viewModel.onEvent(TemplateDetailUiEvent.OnShowDeleteDialogClick)
        viewModel.onEvent(TemplateDetailUiEvent.OnConfirmDeleteClick)

        testDispatcher.scheduler.advanceUntilIdle()
        val effect = viewModel.effect.first()
        assertEquals(TemplateDetailUiSideEffect.NavigateBack, effect)
        assertTrue(repository.templates.isEmpty())
    }
}
