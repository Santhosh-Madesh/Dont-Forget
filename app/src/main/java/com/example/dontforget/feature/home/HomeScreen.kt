package com.example.dontforget.feature.home

import android.text.format.DateFormat
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.dontforget.core.ui.components.ContextIcons
import com.example.dontforget.core.ui.components.WarningBanner
import com.example.dontforget.data.local.model.ReminderWithItems
import kotlinx.coroutines.flow.collectLatest
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToCreateReminder: () -> Unit,
    onNavigateToTemplates: () -> Unit,
    onOpenNotificationSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is HomeUiSideEffect.NavigateToDetail -> onNavigateToDetail(effect.reminderId)
                HomeUiSideEffect.NavigateToCreateReminder -> onNavigateToCreateReminder()
                HomeUiSideEffect.NavigateToTemplates -> onNavigateToTemplates()
                HomeUiSideEffect.OpenNotificationSettings -> onOpenNotificationSettings()
            }
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "DONT FORGET",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = { viewModel.onEvent(HomeUiEvent.OnTemplatesClick) }) {
                        Icon(
                            imageVector = Icons.Filled.Bookmark,
                            contentDescription = "Templates"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors()
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.onEvent(HomeUiEvent.OnCreateReminderClick) }
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "New Reminder"
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (!state.isNotificationPermissionGranted) {
                WarningBanner(
                    message = "Notification permissions required to receive reminders before departure.",
                    actionLabel = "Enable in Settings",
                    onActionClick = { viewModel.onEvent(HomeUiEvent.OnOpenNotificationSettingsClick) }
                )
            }

            if (state.reminders.isEmpty() && !state.isLoading) {
                EmptyHomeContent(
                    onCreateClick = { viewModel.onEvent(HomeUiEvent.OnCreateReminderClick) }
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    items(
                        items = state.reminders,
                        key = { it.reminder.id }
                    ) { reminderWithItems ->
                        ReminderCard(
                            reminderWithItems = reminderWithItems,
                            onClick = {
                                viewModel.onEvent(HomeUiEvent.OnReminderClick(reminderWithItems.reminder.id))
                            }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ReminderCard(
    reminderWithItems: ReminderWithItems,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val reminder = reminderWithItems.reminder
    val items = reminderWithItems.itemsFiltered
    val packedCount = items.count { it.isPacked }
    val totalCount = items.size

    val iconVector = ContextIcons.getIconByName(reminder.contextIconName)
    val formattedDateTime = DateFormat.getDateFormat(context).format(Date(reminder.departureEpochMillis)) +
            " " + DateFormat.getTimeFormat(context).format(Date(reminder.departureEpochMillis))

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = iconVector,
                            contentDescription = reminder.contextTitle,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = reminder.contextTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Leaving at $formattedDateTime",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Surface(
                    shape = MaterialTheme.shapes.extraSmall,
                    color = if (packedCount == totalCount && totalCount > 0)
                        MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        text = "$packedCount/$totalCount Packed",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = if (packedCount == totalCount && totalCount > 0)
                            MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyHomeContent(
    onCreateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.NotificationsActive,
                contentDescription = null,
                modifier = Modifier.size(72.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No Active Reminders",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Add a reminder for your upcoming departure so you don't forget your essential items.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onCreateClick) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Create Reminder")
            }
        }
    }
}
