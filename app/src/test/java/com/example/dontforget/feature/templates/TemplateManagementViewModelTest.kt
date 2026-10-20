package com.example.dontforget.feature.templates

import com.example.dontforget.data.local.entity.ContextEntity
import com.example.dontforget.data.local.entity.TemplateEntity
import com.example.dontforget.feature.templates.management.TemplateManagementUiEvent
import com.example.dontforget.feature.templates.management.TemplateManagementUiSideEffect
import com.example.dontforget.feature.templates.management.TemplateManagementViewModel
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TemplateManagementViewModelTest {

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
    fun `onTemplateClick emits NavigateToDetail effect`() = runTest(testDispatcher) {
        val viewModel = TemplateManagementViewModel(repository)
        viewModel.onEvent(TemplateManagementUiEvent.OnTemplateClick(12L))

        val effect = viewModel.effect.first()
        assertEquals(TemplateManagementUiSideEffect.NavigateToDetail(12L), effect)
    }

    @Test
    fun `onCreateTemplateClick emits NavigateToCreate effect`() = runTest(testDispatcher) {
        val viewModel = TemplateManagementViewModel(repository)
        viewModel.onEvent(TemplateManagementUiEvent.OnCreateTemplateClick)

        val effect = viewModel.effect.first()
        assertEquals(TemplateManagementUiSideEffect.NavigateToCreate, effect)
    }

    @Test
    fun `observeTemplates groups templates by context`() = runTest(testDispatcher) {
        val context1 = ContextEntity(id = 1L, title = "College", iconName = "school")
        val context2 = ContextEntity(id = 2L, title = "Work", iconName = "work")
        repository.contexts.addAll(listOf(context1, context2))

        val template1 = TemplateEntity(id = 1L, contextId = 1L, title = "Exam Prep")
        val template2 = TemplateEntity(id = 2L, contextId = 2L, title = "Office Setup")
        repository.insertTemplate(template1, emptyList())
        repository.insertTemplate(template2, emptyList())

        val viewModel = TemplateManagementViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertFalse(state.isEmpty)
        assertEquals(2, state.groupedTemplates.size)
        assertEquals("College", state.groupedTemplates[0].context.title)
        assertEquals("Work", state.groupedTemplates[1].context.title)
    }
}
