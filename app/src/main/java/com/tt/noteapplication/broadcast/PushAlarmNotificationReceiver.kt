package com.tt.noteapplication.broadcast

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.tt.noteapplication.R
import com.tt.noteapplication.database.NoteRoomDatabase
import com.tt.noteapplication.repository.NoteRepository
import com.tt.noteapplication.util.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class PushAlarmNotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val noteId = intent.getLongExtra(Constants.noteId, 0L)
        if (noteId != 0L) {
            val scope = GlobalScope
            val noteRoomDatabase = NoteRoomDatabase.getDatabase(context)
            val noteRepository = NoteRepository(noteRoomDatabase.noteDao())

            scope.launch(Dispatchers.IO) {
                val noteEntity = noteRepository.getNoteByNoteId(noteId)
                noteEntity?.also {
                    createNotificationChannel(
                        context.getString(R.string.note_notification_channel_id),
                        context.getString(R.string.note_notification_channel_description),
                        context.getString(R.string.note_notification_channel_name),
                        context
                    )
                    val builder = NotificationCompat.Builder(
                        context,
                        context.getString(R.string.note_notification_channel_id)
                    )
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle(it.title)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true)

                    if (it.isLocked) {
                        val sb = StringBuilder(it.content.length - 1)
                        for (i in it.content.indices) {
                            sb.append('*')
                        }
                        builder.setContentText(sb.toString())
                    } else builder.setContentText(it.content)


                    with(NotificationManagerCompat.from(context)) {
                        notify(System.currentTimeMillis().toInt(), builder.build())
                    }
                }
            }
        }
    }

    private fun createNotificationChannel(
        channelId: String,
        description: String,
        channelName: String,
        context: Context
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                this.description = description
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
