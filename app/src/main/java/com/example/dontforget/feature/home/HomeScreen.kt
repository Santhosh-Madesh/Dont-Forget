package com.example.dontforget.feature.home

import android.text.format.DateFormat
import android.util.Log
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Schedule
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.dontforget.R
import com.example.dontforget.core.ui.components.ContextIcons
import com.example.dontforget.core.ui.components.WarningBanner
import com.example.dontforget.data.local.model.ReminderWithItems
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

    androidx.compose.runtime.LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is HomeUiSideEffect.NavigateToDetail -> {
                    onNavigateToDetail(effect.reminderId)
                }
                is HomeUiSideEffect.NavigateToCreateReminder -> {
                    onNavigateToCreateReminder()
                }
                is HomeUiSideEffect.OpenNotificationSettings -> {
                    onOpenNotificationSettings()
                }
            }
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.home_app_bar_title),
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
                    contentDescription = stringResource(id = R.string.home_btn_create_reminder)
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
                            text = stringResource(id = R.string.home_header_title),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Conditional Notification Warning Card
                if (!state.isNotificationPermissionGranted) {
                    item {
                        WarningBanner(
                            message = stringResource(id = R.string.home_notif_banner_message),
                            actionLabel = stringResource(id = R.string.home_notif_banner_action),
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
                                title = stringResource(id = R.string.home_empty_active_title),
                                onCreateClick = { viewModel.onEvent(HomeUiEvent.OnCreateReminderClick) }
                            )
                        }
                    } else {
                        // Overdue Category
                        if (state.overdueReminders.isNotEmpty()) {
                            item {
                                CategoryHeader(
                                    title = stringResource(id = R.string.home_category_overdue),
                                    titleColor = Color(0xFFD97706)
                                )
                            }
                            items(
                                items = state.overdueReminders,
                                key = { "active_overdue_${it.reminder.id}" }
                            ) { reminderWithItems ->
                                Log.d("item rendered", "item_no_${reminderWithItems.reminder.id}")
                                ReminderCard(
                                    reminderWithItems = reminderWithItems,
                                    isOverdue = true,
                                    onClick = {
                                        viewModel.onEvent(HomeUiEvent.OnReminderClick(reminderWithItems.reminder.id))
                                    }
                                )
                            }
                        }

                        // Today Category
                        if (state.todayReminders.isNotEmpty()) {
                            item {
                                CategoryHeader(title = stringResource(id = R.string.home_category_today))
                            }
                            items(
                                items = state.todayReminders,
                                key = { "active_today_${it.reminder.id}" }
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
                                CategoryHeader(title = stringResource(id = R.string.home_category_tomorrow))
                            }
                            items(
                                items = state.tomorrowReminders,
                                key = { "active_tomorrow_${it.reminder.id}" }
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
                                CategoryHeader(title = stringResource(id = R.string.home_category_upcoming))
                            }
                            items(
                                items = state.upcomingReminders,
                                key = { "active_upcoming_${it.reminder.id}" }
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
                                title = stringResource(id = R.string.home_empty_completed_title),
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
        modifier = modifier.fillMaxWidth(),
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
                text = stringResource(id = R.string.home_tab_active),
                isSelected = selectedFilter == HomeFilterTab.ACTIVE,
                onClick = { onFilterTabSelected(HomeFilterTab.ACTIVE) },
                modifier = Modifier.weight(1f)
            )
            FilterSegmentButton(
                text = stringResource(id = R.string.home_tab_completed),
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
        modifier = modifier,
        shape = RoundedCornerShape(50.dp),
        color = if (isSelected)
            MaterialTheme.colorScheme.primary
        else
            Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
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
    modifier: Modifier = Modifier,
    titleColor: Color = MaterialTheme.colorScheme.primary
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = titleColor,
        modifier = modifier.padding(top = 12.dp, bottom = 4.dp)
    )
}

@Composable
fun PackingProgressIndicator(
    packedCount: Int,
    totalCount: Int,
    modifier: Modifier = Modifier
) {
    val isFullyPacked = packedCount == totalCount && totalCount > 0
    val progressRatio = if (totalCount > 0) packedCount.toFloat() / totalCount.toFloat() else 0f

    val trackColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
    val progressColor = if (isFullyPacked) Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary

    Box(
        modifier = modifier.size(40.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isFullyPacked) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidthPx = 3.dp.toPx()
                drawCircle(
                    color = progressColor,
                    style = Stroke(width = strokeWidthPx)
                )
            }
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = stringResource(id = R.string.home_tag_fully_packed),
                tint = progressColor,
                modifier = Modifier.size(20.dp)
            )
        } else {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidthPx = 3.dp.toPx()
                drawCircle(
                    color = trackColor,
                    style = Stroke(width = strokeWidthPx)
                )
                if (progressRatio > 0f) {
                    drawArc(
                        color = progressColor,
                        startAngle = -90f,
                        sweepAngle = 360f * progressRatio,
                        useCenter = false,
                        style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
                    )
                }
            }
            Text(
                text = "$packedCount/$totalCount",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = if (packedCount > 0) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun PackingStatusBadge(
    packedCount: Int,
    totalCount: Int
) {
    if (packedCount == totalCount && totalCount > 0) {
        Surface(
            shape = RoundedCornerShape(50.dp),
            color = Color(0xFFE8F5E9)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = Color(0xFF2E7D32)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = stringResource(id = R.string.home_tag_fully_packed),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32)
                )
            }
        }
    } else if (packedCount == 0) {
        Surface(
            shape = RoundedCornerShape(50.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Text(
                text = stringResource(id = R.string.home_tag_not_started),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
            )
        }
    }
}

@Composable
fun ReminderCard(
    reminderWithItems: ReminderWithItems,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isOverdue: Boolean = false
) {
    val context = LocalContext.current
    val reminder = reminderWithItems.reminder
    val items = reminderWithItems.itemsFiltered
    val packedCount = items.count { it.isPacked }
    val totalCount = items.size

    val iconVector = ContextIcons.getIconByName(reminder.contextIconName)

    val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
    val formattedTime = timeFormat.format(Date(reminder.departureEpochMillis))
    val formattedDate = DateFormat.getDateFormat(context).format(Date(reminder.departureEpochMillis))

    val subtitleText = when {
        isOverdue -> "Overdue · $formattedDate $formattedTime"
        isToday(reminder.departureEpochMillis) -> "Today · $formattedTime"
        isTomorrow(reminder.departureEpochMillis) -> "Tomorrow · $formattedTime"
        else -> "Leaving at $formattedDate $formattedTime"
    }

    val diffMillis = reminder.departureEpochMillis - System.currentTimeMillis()
    val diffMinutes = (diffMillis / (1000 * 60)).toInt()
    val isUrgent = diffMinutes in 1..60

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isOverdue) Color(0xFFFFFBEB) else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header Row: Icon avatar, Title/Subtitle, and Circular Progress
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 1. Context Icon clipped full circle
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = CircleShape,
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

                // 2. Title & Subtitle Column
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = reminder.contextTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitleText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))

                // 3. Circular Progress Ring (3rd item of header row)
                PackingProgressIndicator(
                    packedCount = packedCount,
                    totalCount = totalCount
                )
            }

            // Tags Section (Only displayed if reminder is NOT completed)
            if (!reminder.isCompleted) {
                Spacer(modifier = Modifier.height(12.dp))

                val showPackingBadge = (packedCount == totalCount && totalCount > 0) || (packedCount == 0)

                if (isUrgent) {
                    // Urgent State: Primary tags in row 1, Status badge in separate right-aligned row 2
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Notification Lead Time Pill Tag
                        Surface(
                            shape = RoundedCornerShape(50.dp),
                            color = MaterialTheme.colorScheme.surface
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Notifications,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = stringResource(id = R.string.home_tag_lead_time_mins, reminder.notificationLeadMinutes),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        // Urgency Pill Tag ("Leaves in X min")
                        Surface(
                            shape = RoundedCornerShape(50.dp),
                            color = MaterialTheme.colorScheme.errorContainer
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Schedule,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = MaterialTheme.colorScheme.onErrorContainer
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = stringResource(id = R.string.home_tag_leaves_in_mins, diffMinutes),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }

                    // Row 2: Right-aligned packing status badge
                    if (showPackingBadge) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            PackingStatusBadge(packedCount, totalCount)
                        }
                    }
                } else {
                    // Non-urgent State: Single row
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Notification Lead Time Pill Tag
                        Surface(
                            shape = RoundedCornerShape(50.dp),
                            color = MaterialTheme.colorScheme.surface
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Notifications,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = stringResource(id = R.string.home_tag_lead_time_mins, reminder.notificationLeadMinutes),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        if (showPackingBadge) {
                            Spacer(modifier = Modifier.weight(1f))
                            PackingStatusBadge(packedCount, totalCount)
                        }
                    }
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
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onCreateClick) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = stringResource(id = R.string.home_btn_create_reminder))
            }
        }
    }
}
