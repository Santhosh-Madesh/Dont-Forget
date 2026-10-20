package com.example.dontforget.feature.reminderdetail

import com.example.dontforget.data.local.entity.ReminderEntity
import com.example.dontforget.data.repository.FakeChecklistItemDao
import com.example.dontforget.data.repository.FakeReminderDao
import com.example.dontforget.data.repository.ReminderRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ReminderDetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeReminderDao: FakeReminderDao
    private lateinit var fakeChecklistItemDao: FakeChecklistItemDao
    private lateinit var repository: ReminderRepositoryImpl

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeChecklistItemDao = FakeChecklistItemDao()
        fakeReminderDao = FakeReminderDao { fakeChecklistItemDao }
        repository = ReminderRepositoryImpl(fakeReminderDao, fakeChecklistItemDao)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onShowDeleteDialogClick sets showDeleteDialog to true`() = runTest(testDispatcher) {
        val viewModel = ReminderDetailViewModel(1L, repository)
        viewModel.onEvent(ReminderDetailUiEvent.OnShowDeleteDialogClick)

        assertTrue(viewModel.uiState.value.showDeleteDialog)
    }

    @Test
    fun `onConfirmDeleteClick deletes reminder and emits NavigateBack effect`() = runTest(testDispatcher) {
        fakeReminderDao.insertReminder(ReminderEntity(id = 1L, contextId = 1, contextTitle = "Work", contextIconName = "work", departureEpochMillis = 100))
        val viewModel = ReminderDetailViewModel(1L, repository)

        viewModel.onEvent(ReminderDetailUiEvent.OnConfirmDeleteClick)
        testDispatcher.scheduler.advanceUntilIdle()

        val effect = viewModel.effect.first()
        assertEquals(ReminderDetailUiSideEffect.NavigateBack, effect)
        assertEquals(0, fakeReminderDao.reminders.size)
    }

    @Test
    fun `onToggleItemPacked updates UI state locally without updating repository until onUpdateClick`() = runTest(testDispatcher) {
        val reminder = ReminderEntity(id = 1L, contextId = 1, contextTitle = "Work", contextIconName = "work", departureEpochMillis = 100)
        fakeReminderDao.insertReminder(reminder)
        fakeChecklistItemDao.insertItems(listOf(
            com.example.dontforget.data.local.entity.ChecklistItemEntity(id = 10L, parentId = 1L, parentType = "REMINDER", text = "Keys", isPacked = false)
        ))
        val viewModel = ReminderDetailViewModel(1L, repository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Toggle item
        viewModel.onEvent(ReminderDetailUiEvent.OnToggleItemPacked(10L, true))

        // UI state updated
        assertEquals(true, viewModel.uiState.value.localItems.find { it.id == 10L }?.isPacked)
        // Repo not updated yet
        assertEquals(false, fakeChecklistItemDao.items.find { it.id == 10L }?.isPacked)

        // Click update
        viewModel.onEvent(ReminderDetailUiEvent.OnUpdateClick)
        testDispatcher.scheduler.advanceUntilIdle()

        // Repo updated now
        assertEquals(true, fakeChecklistItemDao.items.find { it.id == 10L }?.isPacked)
    }

    @Test
    fun `onPackAllClick and onUnpackAllClick update local items and are persisted on update`() = runTest(testDispatcher) {
        val reminder = ReminderEntity(id = 1L, contextId = 1, contextTitle = "Work", contextIconName = "work", departureEpochMillis = 100)
        fakeReminderDao.insertReminder(reminder)
        fakeChecklistItemDao.insertItems(listOf(
            com.example.dontforget.data.local.entity.ChecklistItemEntity(id = 10L, parentId = 1L, parentType = "REMINDER", text = "Keys", isPacked = false),
            com.example.dontforget.data.local.entity.ChecklistItemEntity(id = 11L, parentId = 1L, parentType = "REMINDER", text = "Wallet", isPacked = false)
        ))
        val viewModel = ReminderDetailViewModel(1L, repository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Pack All
        viewModel.onEvent(ReminderDetailUiEvent.OnPackAllClick)
        assertTrue(viewModel.uiState.value.isAllPacked)
        assertEquals(false, fakeChecklistItemDao.items.all { it.isPacked })

        // Unpack All
        viewModel.onEvent(ReminderDetailUiEvent.OnUnpackAllClick)
        assertTrue(viewModel.uiState.value.localItems.all { !it.isPacked })

        // Pack All & Update
        viewModel.onEvent(ReminderDetailUiEvent.OnPackAllClick)
        viewModel.onEvent(ReminderDetailUiEvent.OnUpdateClick)
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(fakeChecklistItemDao.items.all { it.isPacked })
    }
}
