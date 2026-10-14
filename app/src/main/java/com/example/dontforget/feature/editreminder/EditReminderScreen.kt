package com.example.dontforget.feature.editreminder

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.dontforget.core.ui.components.ConfirmationDialog
import com.example.dontforget.core.ui.components.ContextIcons
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EditReminderScreen(
    reminderId: Long,
    viewModel: EditReminderViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(reminderId) {
        viewModel.onEvent(EditReminderUiEvent.LoadReminder(reminderId))
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                EditReminderUiSideEffect.NavigateBack -> onNavigateBack()
                is EditReminderUiSideEffect.NavigateToReminderDetail -> onNavigateBack()
                is EditReminderUiSideEffect.ShowToast -> {}
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = "Edit Reminder") },
                navigationIcon = {
                    IconButton(onClick = { viewModel.onEvent(EditReminderUiEvent.RequestDiscard) }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                var newItemText by remember { mutableStateOf("") }
                val calendar = remember(state.departureEpochMillis) {
                    Calendar.getInstance().apply { timeInMillis = state.departureEpochMillis }
                }
                val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault()) }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        AnimatedVisibility(visible = state.validationError != null) {
                            state.validationError?.let { err ->
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.errorContainer,
                                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = err,
                                        modifier = Modifier.padding(12.dp),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = state.contextTitle,
                            onValueChange = { viewModel.onEvent(EditReminderUiEvent.UpdateContextTitle(it)) },
                            label = { Text("Context Title") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    item {
                        Text(text = "Select Icon:", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            ContextIcons.AVAILABLE_ICONS.forEach { item ->
                                val isSelected = state.contextIconName.equals(item.name, ignoreCase = true)
                                Box(
                                    modifier = Modifier
                                        .size(52.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isSelected) MaterialTheme.colorScheme.primaryContainer
                                            else MaterialTheme.colorScheme.surfaceVariant
                                        )
                                        .border(
                                            width = if (isSelected) 2.dp else 0.dp,
                                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                            shape = CircleShape
                                        )
                                        .clickable { viewModel.onEvent(EditReminderUiEvent.SelectContextIcon(item.name)) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = item.label,
                                        tint = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                                        else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Text(text = "Departure Date & Time:", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(imageVector = Icons.Filled.CalendarToday, contentDescription = null)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = dateFormat.format(Date(state.departureEpochMillis)),
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.weight(1f)
                                )
                                TextButton(
                                    onClick = {
                                        DatePickerDialog(
                                            context,
                                            { _, year, month, dayOfMonth ->
                                                calendar.set(Calendar.YEAR, year)
                                                calendar.set(Calendar.MONTH, month)
                                                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                                                TimePickerDialog(
                                                    context,
                                                    { _, hourOfDay, minute ->
                                                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                                                        calendar.set(Calendar.MINUTE, minute)
                                                        calendar.set(Calendar.SECOND, 0)
                                                        viewModel.onEvent(EditReminderUiEvent.SelectDepartureDateTime(calendar.timeInMillis))
                                                    },
                                                    calendar.get(Calendar.HOUR_OF_DAY),
                                                    calendar.get(Calendar.MINUTE),
                                                    false
                                                ).show()
                                            },
                                            calendar.get(Calendar.YEAR),
                                            calendar.get(Calendar.MONTH),
                                            calendar.get(Calendar.DAY_OF_MONTH)
                                        ).show()
                                    }
                                ) {
                                    Text("Change")
                                }
                            }
                        }
                    }

                    item {
                        Text(text = "Notification Lead Time:", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            val presets = listOf(15 to "15m before", 30 to "30m before", 45 to "45m before", 60 to "1h before")
                            presets.forEach { (mins, label) ->
                                val selected = !state.isCustomLeadTime && state.notificationLeadMinutes == mins
                                FilterChip(
                                    selected = selected,
                                    onClick = { viewModel.onEvent(EditReminderUiEvent.SelectNotificationLeadMinutes(mins)) },
                                    label = { Text(label) }
                                )
                            }
                            FilterChip(
                                selected = state.isCustomLeadTime,
                                onClick = { viewModel.onEvent(EditReminderUiEvent.UpdateCustomLeadMinutes(state.customLeadMinutesInput)) },
                                label = { Text("Custom") }
                            )
                        }

                        if (state.isCustomLeadTime) {
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = state.customLeadMinutesInput,
                                onValueChange = { viewModel.onEvent(EditReminderUiEvent.UpdateCustomLeadMinutes(it)) },
                                label = { Text("Lead Time in Minutes") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    item {
                        Text(text = "Checklist Items:", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = newItemText,
                                onValueChange = { newItemText = it },
                                label = { Text("Add Item") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                keyboardActions = KeyboardActions(onDone = {
                                    if (newItemText.isNotBlank()) {
                                        viewModel.onEvent(EditReminderUiEvent.AddChecklistItem(newItemText))
                                        newItemText = ""
                                    }
                                })
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = {
                                    if (newItemText.isNotBlank()) {
                                        viewModel.onEvent(EditReminderUiEvent.AddChecklistItem(newItemText))
                                        newItemText = ""
                                    }
                                }
                            ) {
                                Icon(imageVector = Icons.Filled.Add, contentDescription = "Add")
                            }
                        }
                    }

                    itemsIndexed(state.checklistItems) { index, pair ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "${index + 1}.", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.width(28.dp))
                                OutlinedTextField(
                                    value = pair.second,
                                    onValueChange = { viewModel.onEvent(EditReminderUiEvent.EditChecklistItem(index, it)) },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                                IconButton(
                                    onClick = { if (index > 0) viewModel.onEvent(EditReminderUiEvent.ReorderChecklistItem(index, index - 1)) },
                                    enabled = index > 0
                                ) {
                                    Icon(imageVector = Icons.Filled.ArrowUpward, contentDescription = "Up")
                                }
                                IconButton(
                                    onClick = { if (index < state.checklistItems.lastIndex) viewModel.onEvent(EditReminderUiEvent.ReorderChecklistItem(index, index + 1)) },
                                    enabled = index < state.checklistItems.lastIndex
                                ) {
                                    Icon(imageVector = Icons.Filled.ArrowDownward, contentDescription = "Down")
                                }
                                IconButton(onClick = { viewModel.onEvent(EditReminderUiEvent.RemoveChecklistItem(index)) }) {
                                    Icon(imageVector = Icons.Filled.Delete, contentDescription = "Delete")
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.onEvent(EditReminderUiEvent.SaveChanges) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "Save Changes")
                        }
                    }
                }
            }
        }
    }

    if (state.showDiscardDialog) {
        ConfirmationDialog(
            title = "Discard Changes?",
            message = "Are you sure you want to discard your changes? Unsaved modifications will be lost.",
            confirmText = "Discard",
            dismissText = "Cancel",
            onConfirm = { viewModel.onEvent(EditReminderUiEvent.ConfirmDiscard) },
            onDismiss = { viewModel.onEvent(EditReminderUiEvent.DismissDiscardDialog) }
        )
    }
}
