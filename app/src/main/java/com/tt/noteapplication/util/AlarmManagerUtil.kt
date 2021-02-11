package com.tt.noteapplication.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.tt.noteapplication.broadcast.PushAlarmNotificationReceiver

object AlarmManagerUtil {
    fun setNotification(
        context: Context,
        noteId: Long,
        alarmManager: AlarmManager?,
        time: Long
    ) {
        val myIntent =
            Intent(context, PushAlarmNotificationReceiver::class.java).apply {
                putExtra(Constants.noteId, noteId)
            }

        val pendingIntent = PendingIntent.getBroadcast(
            context, noteId.toInt(), myIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        alarmManager?.set(
            AlarmManager.RTC_WAKEUP,
            time,
            pendingIntent
        )
    }

    fun cancelNotification(
        context: Context,
        noteId: Long,
        alarmManager: AlarmManager?
    ) {
        val myIntent =
            Intent(context, PushAlarmNotificationReceiver::class.java).apply {
            }

        val pendingIntentMorning = PendingIntent.getBroadcast(
            context, noteId.toInt(), myIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )
        if (pendingIntentMorning != null && alarmManager != null) {
            alarmManager.cancel(pendingIntentMorning)
        }
    }
}