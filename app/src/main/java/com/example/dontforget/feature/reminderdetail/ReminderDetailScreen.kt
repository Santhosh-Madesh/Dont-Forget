package com.example.dontforget.feature.reminderdetail

import android.text.format.DateFormat
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.dontforget.core.ui.components.ConfirmationDialog
import com.example.dontforget.core.ui.components.ContextIcons
import com.example.dontforget.data.local.entity.ChecklistItemEntity
import kotlinx.coroutines.flow.collectLatest
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderDetailScreen(
    viewModel: ReminderDetailViewModel,
    onNavigateToEdit: (Long) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showMenu by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is ReminderDetailUiSideEffect.NavigateToEdit -> onNavigateToEdit(effect.reminderId)
                ReminderDetailUiSideEffect.NavigateBack -> onNavigateBack()
                is ReminderDetailUiSideEffect.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    if (state.showDeleteDialog) {
        ConfirmationDialog(
            title = "Delete Reminder?",
            message = "Are you sure you want to delete this reminder? This action cannot be undone.",
            confirmText = "Delete",
            dismissText = "Cancel",
            onConfirm = { viewModel.onEvent(ReminderDetailUiEvent.OnConfirmDeleteClick) },
            onDismiss = { viewModel.onEvent(ReminderDetailUiEvent.OnDismissDeleteDialogClick) }
        )
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(text = "Reminder Overview") },
                navigationIcon = {
                    IconButton(onClick = { viewModel.onEvent(ReminderDetailUiEvent.OnBackClick) }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "Menu")
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Edit") },
                                leadingIcon = { Icon(Icons.Filled.Edit, contentDescription = null) },
                                onClick = {
                                    showMenu = false
                                    viewModel.onEvent(ReminderDetailUiEvent.OnEditReminderClick)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Delete") },
                                leadingIcon = { Icon(Icons.Filled.Delete, contentDescription = null) },
                                onClick = {
                                    showMenu = false
                                    viewModel.onEvent(ReminderDetailUiEvent.OnShowDeleteDialogClick)
                                }
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        val reminder = state.reminderWithItems?.reminder
        if (reminder == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("Loading reminder...")
            }
            return@Scaffold
        }

        val context = LocalContext.current
        val iconVector = ContextIcons.getIconByName(reminder.contextIconName)
        val formattedDateTime = DateFormat.getDateFormat(context).format(Date(reminder.departureEpochMillis)) +
                " at " + DateFormat.getTimeFormat(context).format(Date(reminder.departureEpochMillis))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            // Header Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(48.dp),
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.primary
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = iconVector,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = reminder.contextTitle,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Leaving: $formattedDateTime",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                        Text(
                            text = "Reminder: ${reminder.notificationLeadMinutes} mins before departure",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            // Restore warning banner if completed
            if (state.restoreWarning != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Text(
                        text = state.restoreWarning!!,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Checklist Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Checklist Items",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = { viewModel.onEvent(ReminderDetailUiEvent.OnPackAllClick) }) {
                    Text("Pack All")
                }
            }

            // Checklist Items List
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(
                    items = state.localItems,
                    key = { it.id }
                ) { item ->
                    ChecklistItemRow(
                        item = item,
                        onTogglePacked = { isPacked ->
                            viewModel.onEvent(ReminderDetailUiEvent.OnToggleItemPacked(item.id, isPacked))
                        }
                    )
                }
            }

            // Actions section at bottom
            Spacer(modifier = Modifier.height(12.dp))
            if (state.isAllPacked && !state.isCompleted) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "All items packed!",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { viewModel.onEvent(ReminderDetailUiEvent.OnMarkCompleteClick) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Filled.Check, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Mark as Complete")
                        }
                    }
                }
            }

            if (state.isCompleted) {
                OutlinedButton(
                    onClick = { viewModel.onEvent(ReminderDetailUiEvent.OnRestoreReminderClick) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Text("Restore Reminder")
                }
            } else {
                Button(
                    onClick = { viewModel.onEvent(ReminderDetailUiEvent.OnUpdateClick) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Text("Update")
                }
            }
        }
    }
}

@Composable
fun ChecklistItemRow(
    item: ChecklistItemEntity,
    onTogglePacked: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (item.isPacked)
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = item.isPacked,
                onCheckedChange = onTogglePacked
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = item.text,
                style = MaterialTheme.typography.bodyLarge,
                textDecoration = if (item.isPacked) TextDecoration.LineThrough else TextDecoration.None,
                color = if (item.isPacked)
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
