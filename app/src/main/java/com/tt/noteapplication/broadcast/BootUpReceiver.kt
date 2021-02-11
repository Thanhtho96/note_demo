package com.tt.noteapplication.broadcast

import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.tt.noteapplication.database.NoteRoomDatabase
import com.tt.noteapplication.repository.NoteRepository
import com.tt.noteapplication.util.AlarmManagerUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class BootUpReceiver : BroadcastReceiver() {
    private var alarmManager: AlarmManager? = null

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action === "android.intent.action.BOOT_COMPLETED") {
            alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager

            val scope = GlobalScope
            val noteRoomDatabase = NoteRoomDatabase.getDatabase(context)
            val noteRepository = NoteRepository(noteRoomDatabase.noteDao())

            scope.launch(Dispatchers.IO) {
                val listNoteNotification =
                    noteRepository.getNoteAlarmAppropriate(System.currentTimeMillis())

                for (notification in listNoteNotification) {
                    AlarmManagerUtil.setNotification(
                        context,
                        notification.noteId,
                        alarmManager,
                        notification.alarmTime
                    )
                }
            }
        }
    }
}
