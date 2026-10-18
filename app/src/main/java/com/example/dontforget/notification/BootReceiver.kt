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
