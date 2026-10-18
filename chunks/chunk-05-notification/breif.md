# Android Reminder Notification Feature — Implementation Brief & Guide

This document contains the complete architecture, code implementation, and step-by-step documentation for the Exact Alarm + Single Channel Notification Reminder feature in `Dont Forget` (`com.example.dontforget`).

---

## 1. Overview & Architecture

The notification and exact alarm system consists of the following components in `com.example.dontforget.notification`:
1. **`NotificationConstants`**: Centralized constants for notification channel attributes, intent action names, and extra keys.
2. **`ReminderNotificationManager`**: Manages channel creation, notification construction with deep-link `PendingIntent`s, notification posting, and cancellation.
3. **`ReminderAlarmScheduler`**: Schedules exact alarms using `AlarmManager.setExactAndAllowWhileIdle` and cancels alarms using `PendingIntent.FLAG_NO_CREATE`.
4. **`ReminderBroadcastReceiver`**: Receives `AlarmManager` triggers and delegates notification display to `ReminderNotificationManager`.
5. **`BootReceiver`**: Intercepts `BOOT_COMPLETED` and `MY_PACKAGE_REPLACED` broadcasts to re-initialize notification channels and reschedule active alarms from Room DB.
6. **`ReminderRepositoryImpl` Integration**: Automatically triggers scheduling and cancellation whenever reminders are created, updated, completed, restored, or deleted.
7. **`MainActivity` Integration**: Handles `POST_NOTIFICATIONS` runtime permission launcher, `ON_RESUME` lifecycle checks, and deep-linking directly to `ReminderDetailScreen` on notification click.

---

## 2. AndroidManifest.xml Configuration

Permissions, singleTop launchMode, and broadcast receivers configured in `app/src/main/AndroidManifest.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <!-- Permissions -->
    <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
    <uses-permission android:name="android.permission.USE_EXACT_ALARM" />
    <uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" />
    <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />

    <application
        android:allowBackup="true"
        android:dataExtractionRules="@xml/data_extraction_rules"
        android:fullBackupContent="@xml/backup_rules"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.DontForget">

        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:label="@string/app_name"
            android:launchMode="singleTop"
            android:theme="@style/Theme.DontForget"
            android:windowSoftInputMode="adjustResize">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

        <!-- Broadcast Receivers -->
        <receiver
            android:name=".notification.ReminderBroadcastReceiver"
            android:exported="false" />

        <receiver
            android:name=".notification.BootReceiver"
            android:exported="false">
            <intent-filter>
                <action android:name="android.intent.action.BOOT_COMPLETED" />
                <action android:name="android.intent.action.MY_PACKAGE_REPLACED" />
            </intent-filter>
        </receiver>

    </application>

</manifest>
```

---

## 3. Notification Subsystem Components

### 3.1 Notification Constants (`notification/NotificationConstants.kt`)
```kotlin
package com.example.dontforget.notification

object NotificationConstants {
    const val CHANNEL_ID = "reminder_notification_channel"
    const val CHANNEL_NAME = "Reminder Notifications"
    const val CHANNEL_DESCRIPTION = "Channel for scheduling and receiving reminder alerts"

    const val EXTRA_REMINDER_ID = "extra_reminder_id"
    const val EXTRA_REMINDER_TITLE = "extra_reminder_title"
    const val EXTRA_REMINDER_CONTENT = "extra_reminder_content"
    const val ACTION_TRIGGER_REMINDER = "com.example.dontforget.ACTION_TRIGGER_REMINDER"

    const val DEFAULT_REMINDER_ID = -1L
}
```

### 3.2 Notification Manager (`notification/ReminderNotificationManager.kt`)
```kotlin
package com.example.dontforget.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.dontforget.MainActivity
import com.example.dontforget.R

object ReminderNotificationManager {

    fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NotificationConstants.CHANNEL_ID,
                NotificationConstants.CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = NotificationConstants.CHANNEL_DESCRIPTION
            }

            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showNotification(
        context: Context,
        reminderId: Long,
        title: String,
        content: String
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(NotificationConstants.EXTRA_REMINDER_ID, reminderId)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            reminderId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, NotificationConstants.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title.ifBlank { context.getString(R.string.app_name) })
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        }

        NotificationManagerCompat.from(context).notify(reminderId.toInt(), builder.build())
    }

    fun cancelNotification(context: Context, reminderId: Long) {
        NotificationManagerCompat.from(context).cancel(reminderId.toInt())
    }
}
```

### 3.3 Alarm Scheduler (`notification/ReminderAlarmScheduler.kt`)
```kotlin
package com.example.dontforget.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build

object ReminderAlarmScheduler {

    fun schedule(
        context: Context,
        reminderId: Long,
        title: String,
        content: String,
        triggerTimeMillis: Long
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, ReminderBroadcastReceiver::class.java).apply {
            action = NotificationConstants.ACTION_TRIGGER_REMINDER
            putExtra(NotificationConstants.EXTRA_REMINDER_ID, reminderId)
            putExtra(NotificationConstants.EXTRA_REMINDER_TITLE, title)
            putExtra(NotificationConstants.EXTRA_REMINDER_CONTENT, content)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminderId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTimeMillis,
                    pendingIntent
                )
                return
            }
        }

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerTimeMillis,
            pendingIntent
        )
    }

    fun scheduleForReminder(
        context: Context,
        reminderId: Long,
        title: String,
        departureEpochMillis: Long,
        leadMinutes: Int
    ) {
        val triggerTimeMillis = departureEpochMillis - (leadMinutes * 60 * 1000L)
        if (triggerTimeMillis > System.currentTimeMillis()) {
            schedule(
                context = context,
                reminderId = reminderId,
                title = title,
                content = "Reminder: $title",
                triggerTimeMillis = triggerTimeMillis
            )
        }
    }

    fun cancel(context: Context, reminderId: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, ReminderBroadcastReceiver::class.java).apply {
            action = NotificationConstants.ACTION_TRIGGER_REMINDER
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminderId.toInt(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )

        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }
}
```

### 3.4 Broadcast Receivers (`ReminderBroadcastReceiver` & `BootReceiver`)
```kotlin
package com.example.dontforget.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ReminderBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val reminderId = intent.getLongExtra(
            NotificationConstants.EXTRA_REMINDER_ID,
            NotificationConstants.DEFAULT_REMINDER_ID
        )
        val title = intent.getStringExtra(NotificationConstants.EXTRA_REMINDER_TITLE) ?: ""
        val content = intent.getStringExtra(NotificationConstants.EXTRA_REMINDER_CONTENT) ?: ""

        if (reminderId != NotificationConstants.DEFAULT_REMINDER_ID) {
            ReminderNotificationManager.showNotification(
                context = context,
                reminderId = reminderId,
                title = title,
                content = content
            )
        }
    }
}
```

```kotlin
package com.example.dontforget.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.dontforget.data.local.DontForgetDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == Intent.ACTION_MY_PACKAGE_REPLACED
        ) {
            ReminderNotificationManager.createChannel(context)

            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val db = DontForgetDatabase.getDatabase(context.applicationContext)
                    val activeReminders = db.reminderDao().getActiveReminders().firstOrNull() ?: emptyList()

                    for (item in activeReminders) {
                        val reminder = item.reminder
                        ReminderAlarmScheduler.scheduleForReminder(
                            context = context.applicationContext,
                            reminderId = reminder.id,
                            title = reminder.contextTitle,
                            departureEpochMillis = reminder.departureEpochMillis,
                            leadMinutes = reminder.notificationLeadMinutes
                        )
                    }
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
```

---

## 4. Automatic Scheduling in `ReminderRepositoryImpl`

All reminder CRUD operations automatically manage exact alarms and status bar notifications:

```kotlin
class ReminderRepositoryImpl(
    private val reminderDao: ReminderDao,
    private val checklistItemDao: ChecklistItemDao,
    private val context: Context? = null
) : ReminderRepository {

    override suspend fun insertReminder(reminder: ReminderEntity, items: List<ChecklistItemEntity>): Long {
        val reminderId = reminderDao.insertReminder(reminder)
        // ...
        context?.let { ctx ->
            if (!reminder.isCompleted) {
                ReminderAlarmScheduler.scheduleForReminder(ctx, reminderId, reminder.contextTitle, reminder.departureEpochMillis, reminder.notificationLeadMinutes)
            }
        }
        return reminderId
    }

    override suspend fun updateReminder(reminder: ReminderEntity, items: List<ChecklistItemEntity>) {
        // ...
        context?.let { ctx ->
            ReminderAlarmScheduler.cancel(ctx, reminder.id)
            if (!reminder.isCompleted) {
                ReminderAlarmScheduler.scheduleForReminder(ctx, reminder.id, reminder.contextTitle, reminder.departureEpochMillis, reminder.notificationLeadMinutes)
            }
        }
    }

    override suspend fun markReminderComplete(reminderId: Long, completedEpochMillis: Long) {
        reminderDao.markCompleted(reminderId, completedEpochMillis)
        context?.let { ctx ->
            ReminderAlarmScheduler.cancel(ctx, reminderId)
            ReminderNotificationManager.cancelNotification(ctx, reminderId)
        }
    }

    override suspend fun restoreReminder(reminderId: Long) {
        reminderDao.restoreReminder(reminderId)
        context?.let { ctx ->
            val reminder = reminderDao.getReminderEntityById(reminderId)
            if (reminder != null) {
                ReminderAlarmScheduler.scheduleForReminder(ctx, reminder.id, reminder.contextTitle, reminder.departureEpochMillis, reminder.notificationLeadMinutes)
            }
        }
    }

    override suspend fun deleteReminder(reminderId: Long) {
        context?.let { ctx ->
            ReminderAlarmScheduler.cancel(ctx, reminderId)
            ReminderNotificationManager.cancelNotification(ctx, reminderId)
        }
        // ...
    }
}
```

---

## 5. UI & Deep-Linking Flow (`MainActivity.kt`)

### Deep Linking Navigation on Click:
- `handleIntent(intent)` captures `EXTRA_REMINDER_ID` during `onCreate` or `onNewIntent`.
- `LaunchedEffect(pendingReminderId)` automatically routes to `Route.ReminderDetail.createRoute(reminderId)`.

### Permission Request & Auto-Dismiss:
- Uses `rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission())` to launch runtime permission dialog on Android 13+.
- Uses `DisposableEffect` with `LifecycleEventObserver` listening to `ON_RESUME` to re-check permissions and dismiss the warning banner as soon as the user returns from system settings.
