package com.example.dontforget.notification

object NotificationConstants {
    // Channel Configuration
    const val CHANNEL_ID = "reminder_notification_channel"
    const val CHANNEL_NAME = "Reminder Notifications"
    const val CHANNEL_DESCRIPTION = "Channel for scheduling and receiving reminder alerts"

    // Intent Extras & Actions
    const val EXTRA_REMINDER_ID = "extra_reminder_id"
    const val EXTRA_REMINDER_TITLE = "extra_reminder_title"
    const val EXTRA_REMINDER_CONTENT = "extra_reminder_content"
    const val ACTION_TRIGGER_REMINDER = "com.example.dontforget.ACTION_TRIGGER_REMINDER"

    const val DEFAULT_REMINDER_ID = -1L
}
