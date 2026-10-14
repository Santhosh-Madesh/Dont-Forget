# ViewModel — Chunk 01: Reminders & Home Screen (`chunk-01-home-and-reminders`)

## 1. HomeViewModel
- Extends `BaseViewModel<HomeUiState, HomeUiEvent, HomeUiSideEffect>`.
- Collects `ReminderRepository.getActiveReminders()` in `viewModelScope` and updates `HomeUiState.reminders`.

## 2. ReminderDetailViewModel
- Extends `BaseViewModel<ReminderDetailUiState, ReminderDetailUiEvent, ReminderDetailUiSideEffect>`.
- Takes `reminderId: Long` and `ReminderRepository`.
- Collects `ReminderRepository.getReminderById(reminderId)` and updates `ReminderDetailUiState`.
- Handles packing, completion, deletion, and restore actions.
