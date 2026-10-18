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
