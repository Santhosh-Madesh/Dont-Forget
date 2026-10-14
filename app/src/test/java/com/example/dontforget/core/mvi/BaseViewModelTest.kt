package com.example.dontforget.core.mvi

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

data class TestState(val count: Int = 0) : UiState
sealed interface TestEvent : UiEvent {
    object Increment : TestEvent
    object TriggerEffect : TestEvent
}
sealed interface TestEffect : UiSideEffect {
    object Toast : TestEffect
}

class TestViewModel : BaseViewModel<TestState, TestEvent, TestEffect>(TestState()) {
    override fun onEvent(event: TestEvent) {
        when (event) {
            TestEvent.Increment -> updateState { copy(count = count + 1) }
            TestEvent.TriggerEffect -> sendEffect(TestEffect.Toast)
        }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class BaseViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is correct`() {
        val viewModel = TestViewModel()
        assertEquals(0, viewModel.uiState.value.count)
    }

    @Test
    fun `event updates uiState correctly`() {
        val viewModel = TestViewModel()
        viewModel.onEvent(TestEvent.Increment)
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(1, viewModel.uiState.value.count)
    }

    @Test
    fun `event triggers effect channel correctly`() = runTest(testDispatcher) {
        val viewModel = TestViewModel()
        viewModel.onEvent(TestEvent.TriggerEffect)
        val effect = viewModel.effect.first()
        assertEquals(TestEffect.Toast, effect)
    }
}
