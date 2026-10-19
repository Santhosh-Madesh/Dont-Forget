package com.example.dontforget.feature.home

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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.Calendar

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
    fun `onOpenNotificationSettingsClick emits OpenNotificationSettings effect`() = runTest(testDispatcher) {
        val viewModel = HomeViewModel(repository)
        viewModel.onEvent(HomeUiEvent.OnOpenNotificationSettingsClick)

        val effect = viewModel.effect.first()
        assertEquals(HomeUiSideEffect.OpenNotificationSettings, effect)
    }

    @Test
    fun `onFilterTabSelected updates selectedFilter in HomeUiState`() = runTest(testDispatcher) {
        val viewModel = HomeViewModel(repository)
        assertEquals(HomeFilterTab.ACTIVE, viewModel.uiState.value.selectedFilter)

        viewModel.onEvent(HomeUiEvent.OnFilterTabSelected(HomeFilterTab.COMPLETED))
        assertEquals(HomeFilterTab.COMPLETED, viewModel.uiState.value.selectedFilter)
    }

    @Test
    fun `onNotificationPermissionStatusChanged updates isNotificationPermissionGranted`() = runTest(testDispatcher) {
        val viewModel = HomeViewModel(repository)
        assertTrue(viewModel.uiState.value.isNotificationPermissionGranted)

        viewModel.onEvent(HomeUiEvent.OnNotificationPermissionStatusChanged(false))
        assertFalse(viewModel.uiState.value.isNotificationPermissionGranted)
    }

    @Test
    fun `categorization properties correctly partition active reminders`() = runTest(testDispatcher) {
        val calOverdue = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -2) }
        val calToday = Calendar.getInstance()
        val calTomorrow = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }
        val calUpcoming = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 3) }

        val overdueReminder = ReminderEntity(id = 0, contextId = 1, contextTitle = "Past Task", contextIconName = "work", departureEpochMillis = calOverdue.timeInMillis)
        val todayReminder = ReminderEntity(id = 1, contextId = 1, contextTitle = "Work Today", contextIconName = "work", departureEpochMillis = calToday.timeInMillis)
        val tomorrowReminder = ReminderEntity(id = 2, contextId = 1, contextTitle = "Gym Tomorrow", contextIconName = "gym", departureEpochMillis = calTomorrow.timeInMillis)
        val upcomingReminder = ReminderEntity(id = 3, contextId = 1, contextTitle = "Flight Upcoming", contextIconName = "flight", departureEpochMillis = calUpcoming.timeInMillis)

        fakeReminderDao.insertReminder(overdueReminder)
        fakeReminderDao.insertReminder(todayReminder)
        fakeReminderDao.insertReminder(tomorrowReminder)
        fakeReminderDao.insertReminder(upcomingReminder)

        val viewModel = HomeViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(4, state.activeReminders.size)
        assertEquals(1, state.overdueReminders.size)
        assertEquals(1, state.todayReminders.size)
        assertEquals(1, state.tomorrowReminders.size)
        assertEquals(1, state.upcomingReminders.size)

        assertEquals("Past Task", state.overdueReminders[0].reminder.contextTitle)
        assertEquals("Work Today", state.todayReminders[0].reminder.contextTitle)
        assertEquals("Gym Tomorrow", state.tomorrowReminders[0].reminder.contextTitle)
        assertEquals("Flight Upcoming", state.upcomingReminders[0].reminder.contextTitle)
    }
}
