package com.example.dontforget.feature.editreminder

import com.example.dontforget.data.local.entity.ChecklistItemEntity
import com.example.dontforget.data.local.entity.ReminderEntity
import com.example.dontforget.feature.createreminder.FakeReminderRepository
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
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class EditReminderViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeReminderRepo: FakeReminderRepository
    private lateinit var viewModel: EditReminderViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeReminderRepo = FakeReminderRepository()
        viewModel = EditReminderViewModel(fakeReminderRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadReminder populates state from repository`() = runTest(testDispatcher) {
        val futureTime = System.currentTimeMillis() + 100000L
        val rem = ReminderEntity(id = 1L, contextId = 1L, contextTitle = "Work", contextIconName = "work", departureEpochMillis = futureTime, notificationLeadMinutes = 15)
        val items = listOf(ChecklistItemEntity(id = 10L, parentId = 1L, parentType = "REMINDER", text = "Laptop"))
        fakeReminderRepo.insertReminder(rem, items)

        viewModel.onEvent(EditReminderUiEvent.LoadReminder(1L))
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("Work", state.contextTitle)
        assertEquals("work", state.contextIconName)
        assertEquals(1, state.checklistItems.size)
        assertEquals("Laptop", state.checklistItems[0].second)
        assertFalse(state.isLoading)
    }

    @Test
    fun `saveChanges updates repository and sends NavigateBack effect`() = runTest(testDispatcher) {
        val futureTime = System.currentTimeMillis() + 100000L
        val rem = ReminderEntity(id = 1L, contextId = 1L, contextTitle = "Work", contextIconName = "work", departureEpochMillis = futureTime, notificationLeadMinutes = 15)
        val items = listOf(ChecklistItemEntity(id = 10L, parentId = 1L, parentType = "REMINDER", text = "Laptop"))
        fakeReminderRepo.insertReminder(rem, items)

        viewModel.onEvent(EditReminderUiEvent.LoadReminder(1L))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onEvent(EditReminderUiEvent.UpdateContextTitle("Office Work"))
        viewModel.onEvent(EditReminderUiEvent.AddChecklistItem("Badge"))
        viewModel.onEvent(EditReminderUiEvent.SaveChanges)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("Office Work", fakeReminderRepo.reminders[0].contextTitle)
        assertEquals(2, fakeReminderRepo.items.size)

        val effect = viewModel.effect.first()
        assertEquals(EditReminderUiSideEffect.NavigateBack, effect)
    }

    @Test
    fun `saveChanges fails if departure time is in past`() = runTest(testDispatcher) {
        val pastTime = System.currentTimeMillis() - 100000L
        val rem = ReminderEntity(id = 1L, contextId = 1L, contextTitle = "Work", contextIconName = "work", departureEpochMillis = pastTime, notificationLeadMinutes = 15)
        val items = listOf(ChecklistItemEntity(id = 10L, parentId = 1L, parentType = "REMINDER", text = "Laptop"))
        fakeReminderRepo.insertReminder(rem, items)

        viewModel.onEvent(EditReminderUiEvent.LoadReminder(1L))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onEvent(EditReminderUiEvent.SaveChanges)
        testDispatcher.scheduler.advanceUntilIdle()

        assertNotNull(viewModel.uiState.value.validationError)
    }
}
