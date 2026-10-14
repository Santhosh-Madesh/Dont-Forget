package com.example.dontforget.feature.home

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
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

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
    fun `onReminderClick emits NavigateToDetail effect`() = runTest(testDispatcher) {
        val viewModel = HomeViewModel(repository)
        viewModel.onEvent(HomeUiEvent.OnReminderClick(123L))

        val effect = viewModel.effect.first()
        assertEquals(HomeUiSideEffect.NavigateToDetail(123L), effect)
    }

    @Test
    fun `onCreateReminderClick emits NavigateToCreateReminder effect`() = runTest(testDispatcher) {
        val viewModel = HomeViewModel(repository)
        viewModel.onEvent(HomeUiEvent.OnCreateReminderClick)

        val effect = viewModel.effect.first()
        assertEquals(HomeUiSideEffect.NavigateToCreateReminder, effect)
    }

    @Test
    fun `onTemplatesClick emits NavigateToTemplates effect`() = runTest(testDispatcher) {
        val viewModel = HomeViewModel(repository)
        viewModel.onEvent(HomeUiEvent.OnTemplatesClick)

        val effect = viewModel.effect.first()
        assertEquals(HomeUiSideEffect.NavigateToTemplates, effect)
    }
}
