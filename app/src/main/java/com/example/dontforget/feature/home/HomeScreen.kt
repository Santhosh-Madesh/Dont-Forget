package com.example.dontforget.feature.home

import android.text.format.DateFormat
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Notifications
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.dontforget.core.ui.components.ContextIcons
import com.example.dontforget.core.ui.components.WarningBanner
import com.example.dontforget.data.local.model.ReminderWithItems
import kotlinx.coroutines.flow.collectLatest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToCreateReminder: () -> Unit,
    onOpenNotificationSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is HomeUiSideEffect.NavigateToDetail -> onNavigateToDetail(effect.reminderId)
                HomeUiSideEffect.NavigateToCreateReminder -> onNavigateToCreateReminder()
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
                    IconButton(onClick = { viewModel.onEvent(HomeUiEvent.OnNotificationIconClick) }) {
                        Icon(
                            imageVector = Icons.Filled.Notifications,
                            contentDescription = "Notifications"
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Today's Date & Reminders Title Header
                item {
                    val formattedDate = SimpleDateFormat("EEEE, MMM d", Locale.getDefault()).format(Date())
                    Column(modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)) {
                        Text(
                            text = formattedDate,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Reminders",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Conditional Notification Warning Card
                if (!state.isNotificationPermissionGranted) {
                    item {
                        WarningBanner(
                            message = "Notification permissions required to receive reminders before departure.",
                            actionLabel = "Enable in Settings",
                            onActionClick = { viewModel.onEvent(HomeUiEvent.OnOpenNotificationSettingsClick) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // Active / Completed Segmented Filter Toggle
                item {
                    SegmentedFilterToggle(
                        selectedFilter = state.selectedFilter,
                        onFilterTabSelected = { filterTab ->
                            viewModel.onEvent(HomeUiEvent.OnFilterTabSelected(filterTab))
                        }
                    )
                }

                // Content View Based on Selected Filter
                if (state.selectedFilter == HomeFilterTab.ACTIVE) {
                    if (state.activeReminders.isEmpty() && !state.isLoading) {
                        item {
                            EmptyHomeContent(
                                title = "No Active Reminders",
                                onCreateClick = { viewModel.onEvent(HomeUiEvent.OnCreateReminderClick) }
                            )
                        }
                    } else {
                        // Today Category
                        if (state.todayReminders.isNotEmpty()) {
                            item {
                                CategoryHeader(title = "TODAY")
                            }
                            items(
                                items = state.todayReminders,
                                key = { "active_${it.reminder.id}" }
                            ) { reminderWithItems ->
                                ReminderCard(
                                    reminderWithItems = reminderWithItems,
                                    onClick = {
                                        viewModel.onEvent(HomeUiEvent.OnReminderClick(reminderWithItems.reminder.id))
                                    }
                                )
                            }
                        }

                        // Tomorrow Category
                        if (state.tomorrowReminders.isNotEmpty()) {
                            item {
                                CategoryHeader(title = "TOMORROW")
                            }
                            items(
                                items = state.tomorrowReminders,
                                key = { "active_${it.reminder.id}" }
                            ) { reminderWithItems ->
                                ReminderCard(
                                    reminderWithItems = reminderWithItems,
                                    onClick = {
                                        viewModel.onEvent(HomeUiEvent.OnReminderClick(reminderWithItems.reminder.id))
                                    }
                                )
                            }
                        }

                        // Upcoming Category
                        if (state.upcomingReminders.isNotEmpty()) {
                            item {
                                CategoryHeader(title = "UPCOMING")
                            }
                            items(
                                items = state.upcomingReminders,
                                key = { "active_${it.reminder.id}" }
                            ) { reminderWithItems ->
                                ReminderCard(
                                    reminderWithItems = reminderWithItems,
                                    onClick = {
                                        viewModel.onEvent(HomeUiEvent.OnReminderClick(reminderWithItems.reminder.id))
                                    }
                                )
                            }
                        }
                    }
                } else {
                    // Completed Filter Selected
                    if (state.completedReminders.isEmpty() && !state.isLoading) {
                        item {
                            EmptyHomeContent(
                                title = "No Completed Reminders",
                                onCreateClick = { viewModel.onEvent(HomeUiEvent.OnCreateReminderClick) }
                            )
                        }
                    } else {
                        items(
                            items = state.completedReminders,
                            key = { "completed_${it.reminder.id}" }
                        ) { reminderWithItems ->
                            ReminderCard(
                                reminderWithItems = reminderWithItems,
                                onClick = {
                                    viewModel.onEvent(HomeUiEvent.OnReminderClick(reminderWithItems.reminder.id))
                                }
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

@Composable
fun SegmentedFilterToggle(
    selectedFilter: HomeFilterTab,
    onFilterTabSelected: (HomeFilterTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
        ,
        shape = RoundedCornerShape(50.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .padding(4.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            FilterSegmentButton(
                text = "Active",
                isSelected = selectedFilter == HomeFilterTab.ACTIVE,
                onClick = { onFilterTabSelected(HomeFilterTab.ACTIVE) },
                modifier = Modifier.weight(1f)
            )
            FilterSegmentButton(
                text = "Completed",
                isSelected = selectedFilter == HomeFilterTab.COMPLETED,
                onClick = { onFilterTabSelected(HomeFilterTab.COMPLETED) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun FilterSegmentButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier
        ,
        shape = RoundedCornerShape(50.dp),
        color = if (isSelected)
            MaterialTheme.colorScheme.primary
        else
            Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                ,
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                textAlign = TextAlign.Center,
                color = if (isSelected)
                    MaterialTheme.colorScheme.onPrimary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun CategoryHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier.padding(top = 12.dp, bottom = 4.dp)
    )
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

    val diffMillis = reminder.departureEpochMillis - System.currentTimeMillis()
    val diffMinutes = (diffMillis / (1000 * 60)).toInt()
    val isUrgent = diffMinutes in 1..60

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
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Tags & Status Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Notification Lead Time Tag
                Surface(
                    shape = MaterialTheme.shapes.extraSmall,
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Notifications,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${reminder.notificationLeadMinutes} min before",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Urgency Tag ("Leaves in X min")
                if (isUrgent) {
                    Surface(
                        shape = MaterialTheme.shapes.extraSmall,
                        color = MaterialTheme.colorScheme.errorContainer
                    ) {
                        Text(
                            text = "Leaves in $diffMinutes min",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Packing Status Tag
                val (statusText, badgeBg, badgeFg) = when {
                    packedCount == totalCount && totalCount > 0 -> Triple(
                        "Fully packed",
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.onPrimary
                    )
                    packedCount == 0 -> Triple(
                        "Not started",
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.onSurface
                    )
                    else -> Triple(
                        "$packedCount/$totalCount Packed",
                        MaterialTheme.colorScheme.secondaryContainer,
                        MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }

                Surface(
                    shape = MaterialTheme.shapes.extraSmall,
                    color = badgeBg
                ) {
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.labelMedium,
                        color = badgeFg,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyHomeContent(
    title: String,
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
                text = title,
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
