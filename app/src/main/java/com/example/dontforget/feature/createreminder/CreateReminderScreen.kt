package com.example.dontforget.feature.createreminder

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
fun CreateReminderScreen(
    viewModel: CreateReminderViewModel,
    onNavigateHome: () -> Unit,
    onNavigateToReminderDetail: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                CreateReminderUiSideEffect.NavigateHome -> onNavigateHome()
                is CreateReminderUiSideEffect.NavigateToReminderDetail -> onNavigateToReminderDetail(effect.reminderId)
                is CreateReminderUiSideEffect.ShowToast -> {}
            }
        }
    }

    val title = when (state.currentStep) {
        CreateReminderStep.SELECT_TEMPLATE -> "Select Template"
        CreateReminderStep.CONTEXT -> "Context Details"
        CreateReminderStep.CHECKLIST -> "Checklist Items"
        CreateReminderStep.DATE_TIME_NOTIFICATION -> "Departure & Notification"
        CreateReminderStep.PREVIEW -> "Reminder Preview"
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = title) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.onEvent(CreateReminderUiEvent.PreviousStep) }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(onClick = { viewModel.onEvent(CreateReminderUiEvent.RequestDiscard) }) {
                        Text(text = "Cancel")
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Step Indicator Progress Text
                Text(
                    text = "Step ${state.currentStep.ordinal + 1} of 5",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Error Banner
                AnimatedVisibility(visible = state.validationError != null) {
                    state.validationError?.let { err ->
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                                contentColor = MaterialTheme.colorScheme.onErrorContainer
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                        ) {
                            Text(
                                text = err,
                                modifier = Modifier.padding(12.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                // Step Content
                Box(modifier = Modifier.weight(1f)) {
                    when (state.currentStep) {
                        CreateReminderStep.SELECT_TEMPLATE -> StepSelectTemplate(
                            state = state,
                            onEvent = viewModel::onEvent
                        )
                        CreateReminderStep.CONTEXT -> StepContext(
                            state = state,
                            onEvent = viewModel::onEvent
                        )
                        CreateReminderStep.CHECKLIST -> StepChecklist(
                            state = state,
                            onEvent = viewModel::onEvent
                        )
                        CreateReminderStep.DATE_TIME_NOTIFICATION -> StepDateTimeNotification(
                            state = state,
                            onEvent = viewModel::onEvent
                        )
                        CreateReminderStep.PREVIEW -> StepPreview(
                            state = state,
                            onEvent = viewModel::onEvent
                        )
                    }
                }
            }

            if (state.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }

    if (state.showDiscardDialog) {
        ConfirmationDialog(
            title = "Discard Reminder?",
            message = "Are you sure you want to discard this reminder? All unsaved changes will be lost.",
            confirmText = "Discard",
            dismissText = "Continue Editing",
            onConfirm = { viewModel.onEvent(CreateReminderUiEvent.ConfirmDiscard) },
            onDismiss = { viewModel.onEvent(CreateReminderUiEvent.DismissDiscardDialog) }
        )
    }
}

@Composable
private fun StepSelectTemplate(
    state: CreateReminderUiState,
    onEvent: (CreateReminderUiEvent) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedButton(
            onClick = { onEvent(CreateReminderUiEvent.SkipTemplate) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Create Custom Reminder from Scratch")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Or select a Template:",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (state.availableTemplates.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No saved templates found.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.availableTemplates) { templateWithItems ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onEvent(CreateReminderUiEvent.SelectTemplate(templateWithItems.template.id)) },
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = ContextIcons.getIconByName(templateWithItems.context.iconName),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = templateWithItems.template.title,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = "${templateWithItems.context.title} • ${templateWithItems.items.size} items",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun StepContext(
    state: CreateReminderUiState,
    onEvent: (CreateReminderUiEvent) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = state.contextTitle,
            onValueChange = { onEvent(CreateReminderUiEvent.UpdateContextTitle(it)) },
            label = { Text("Context Title (e.g. College, Work, Gym)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Select Icon:",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            ContextIcons.AVAILABLE_ICONS.forEach { item ->
                val isSelected = state.contextIconName.equals(item.name, ignoreCase = true)
                Box(
                    modifier = Modifier
                        .size(56.dp)
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
                        .clickable { onEvent(CreateReminderUiEvent.SelectContextIcon(item.name)) },
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

        Button(
            onClick = { onEvent(CreateReminderUiEvent.NextStep) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Next: Checklist Items")
        }
    }
}

@Composable
private fun StepChecklist(
    state: CreateReminderUiState,
    onEvent: (CreateReminderUiEvent) -> Unit
) {
    var newItemText by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = newItemText,
                onValueChange = { newItemText = it },
                label = { Text("Add Checklist Item") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    if (newItemText.isNotBlank()) {
                        onEvent(CreateReminderUiEvent.AddChecklistItem(newItemText))
                        newItemText = ""
                    }
                })
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = {
                    if (newItemText.isNotBlank()) {
                        onEvent(CreateReminderUiEvent.AddChecklistItem(newItemText))
                        newItemText = ""
                    }
                }
            ) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "Add Item")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (state.checklistItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No items added yet. Add at least 1 item to proceed.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(state.checklistItems) { index, itemText ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${index + 1}.",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.width(28.dp)
                            )
                            Text(
                                text = itemText,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = {
                                    if (index > 0) onEvent(CreateReminderUiEvent.ReorderChecklistItem(index, index - 1))
                                },
                                enabled = index > 0
                            ) {
                                Icon(imageVector = Icons.Filled.ArrowUpward, contentDescription = "Move Up")
                            }
                            IconButton(
                                onClick = {
                                    if (index < state.checklistItems.lastIndex) onEvent(CreateReminderUiEvent.ReorderChecklistItem(index, index + 1))
                                },
                                enabled = index < state.checklistItems.lastIndex
                            ) {
                                Icon(imageVector = Icons.Filled.ArrowDownward, contentDescription = "Move Down")
                            }
                            IconButton(
                                onClick = { onEvent(CreateReminderUiEvent.RemoveChecklistItem(index)) }
                            ) {
                                Icon(imageVector = Icons.Filled.Delete, contentDescription = "Remove")
                            }
                        }
                    }
                }
            }
        }

        Button(
            onClick = { onEvent(CreateReminderUiEvent.NextStep) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Next: Departure & Notification")
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun StepDateTimeNotification(
    state: CreateReminderUiState,
    onEvent: (CreateReminderUiEvent) -> Unit
) {
    val context = LocalContext.current
    val calendar = remember(state.departureEpochMillis) {
        Calendar.getInstance().apply { timeInMillis = state.departureEpochMillis }
    }

    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault()) }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Departure Date & Time:",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

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
                                        onEvent(CreateReminderUiEvent.SelectDepartureDateTime(calendar.timeInMillis))
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

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Notification Lead Time:",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

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
                    onClick = { onEvent(CreateReminderUiEvent.SelectNotificationLeadMinutes(mins)) },
                    label = { Text(label) }
                )
            }
            FilterChip(
                selected = state.isCustomLeadTime,
                onClick = { onEvent(CreateReminderUiEvent.UpdateCustomLeadMinutes(state.customLeadMinutesInput)) },
                label = { Text("Custom") }
            )
        }

        if (state.isCustomLeadTime) {
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = state.customLeadMinutesInput,
                onValueChange = { onEvent(CreateReminderUiEvent.UpdateCustomLeadMinutes(it)) },
                label = { Text("Lead Time in Minutes") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { onEvent(CreateReminderUiEvent.NextStep) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Next: Preview")
        }
    }
}

@Composable
private fun StepPreview(
    state: CreateReminderUiState,
    onEvent: (CreateReminderUiEvent) -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault()) }

    Column(modifier = Modifier.fillMaxSize()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = ContextIcons.getIconByName(state.contextIconName),
                        contentDescription = null,
                        modifier = Modifier.size(36.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = state.contextTitle,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.CalendarToday,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Departure: ${dateFormat.format(Date(state.departureEpochMillis))}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Schedule,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Notification: ${state.notificationLeadMinutes} mins before departure",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Checklist Items (${state.checklistItems.size}):",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(state.checklistItems) { item ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "• ",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = item,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Checkbox(
                checked = state.saveAsTemplate,
                onCheckedChange = { onEvent(CreateReminderUiEvent.ToggleSaveAsTemplate(it)) }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Save modified checklist as a new template",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        if (state.saveAsTemplate) {
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = state.templateTitleInput,
                onValueChange = { onEvent(CreateReminderUiEvent.UpdateTemplateTitle(it)) },
                label = { Text("Template Title") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onEvent(CreateReminderUiEvent.SaveReminder) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Confirm & Create Reminder")
        }
    }
}
