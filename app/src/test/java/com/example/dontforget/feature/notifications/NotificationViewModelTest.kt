package com.example.dontforget.feature.notifications

import com.example.dontforget.data.local.entity.ReminderEntity
import com.example.dontforget.data.repository.FakeChecklistItemDao
import com.example.dontforget.data.repository.FakeReminderDao
import com.example.dontforget.data.repository.ReminderRepositoryImpl
import com.example.dontforget.feature.notifications.screen.NotificationUiEvent
import com.example.dontforget.feature.notifications.screen.NotificationUiSideEffect
import com.example.dontforget.feature.notifications.screen.NotificationViewModel
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
class NotificationViewModelTest {

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
    fun `onBackClick emits NavigateBack effect`() = runTest(testDispatcher) {
        val viewModel = NotificationViewModel(repository)
        viewModel.onEvent(NotificationUiEvent.OnBackClick)

        val effect = viewModel.effect.first()
        assertEquals(NotificationUiSideEffect.NavigateBack, effect)
    }

    @Test
    fun `empty repository results in empty state`() = runTest(testDispatcher) {
        val viewModel = NotificationViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertTrue(state.isEmpty)
        assertEquals(0, state.upcomingCount)
        assertEquals(0, state.previousCount)
    }

    @Test
    fun `observeReminders correctly splits upcoming and previous notifications`() = runTest(testDispatcher) {
        // Future departure (tomorrow) with 30 min lead time -> Upcoming
        val futureCal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }
        val futureReminder = ReminderEntity(
            id = 1,
            contextId = 1,
            contextTitle = "College Essentials",
            contextIconName = "school",
            departureEpochMillis = futureCal.timeInMillis,
            notificationLeadMinutes = 30
        )

        // Past departure (yesterday) with 30 min lead time -> Previous
        val pastCal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
        val pastReminder = ReminderEntity(
            id = 2,
            contextId = 2,
            contextTitle = "Office Documents",
            contextIconName = "work",
            departureEpochMillis = pastCal.timeInMillis,
            notificationLeadMinutes = 30
        )

        fakeReminderDao.insertReminder(futureReminder)
        fakeReminderDao.insertReminder(pastReminder)

        val viewModel = NotificationViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertFalse(state.isEmpty)
        assertEquals(1, state.upcomingCount)
        assertEquals(1, state.previousCount)

        assertEquals("College Essentials", state.upcomingNotifications[0].title)
        assertEquals("Reminder: College Essentials", state.upcomingNotifications[0].content)

        assertEquals("Office Documents", state.previousNotifications[0].title)
        assertEquals("Reminder: Office Documents", state.previousNotifications[0].content)
    }
}
