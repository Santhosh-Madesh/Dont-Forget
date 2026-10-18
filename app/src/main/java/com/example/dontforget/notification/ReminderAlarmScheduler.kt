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
