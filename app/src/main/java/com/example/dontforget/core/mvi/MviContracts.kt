package com.example.dontforget.core.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

interface UiState

interface UiEvent

interface UiSideEffect

abstract class BaseViewModel<S : UiState, E : UiEvent, SE : UiSideEffect>(
    initialState: S
) : ViewModel() {

    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<S> = _uiState.asStateFlow()

    private val _effect = Channel<SE>(Channel.BUFFERED)
    val effect: Flow<SE> = _effect.receiveAsFlow()

    protected val currentState: S
        get() = _uiState.value

    abstract fun onEvent(event: E)

    protected fun updateState(reducer: S.() -> S) {
        _uiState.update(reducer)
    }

    protected fun sendEffect(effect: SE) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
}
