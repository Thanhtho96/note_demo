package com.tt.noteapplication.ui

import android.app.AlarmManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import com.tt.noteapplication.R
import com.tt.noteapplication.model.AlarmOfNoteEntity
import com.tt.noteapplication.util.AlarmManagerUtil
import com.tt.noteapplication.util.Constants
import com.tt.noteapplication.viewmodel.NoteViewModel
import kotlinx.android.synthetic.main.fragment_set_notification_dialog.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.*

class SetNotificationDialogFragment : DialogFragment(), View.OnClickListener {

    private val noteViewModel by sharedViewModel<NoteViewModel>()
    private var noteId: Long? = null
    private var alarmId: Long? = null
    private var alarmTime = 0L
    private var alarmManager: AlarmManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        noteId = arguments?.getLong(Constants.noteId)
        alarmTime = arguments?.getLong(Constants.alarmTime)!!

        alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as? AlarmManager
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.setCanceledOnTouchOutside(true)
        return inflater.inflate(R.layout.fragment_set_notification_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val now = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
        }

        noteId?.run {
            if (noteId == 0L) {
                if (alarmTime == 0L) {
                    initCalendarAndTimePicker(now)
                    date_picker.minDate = now.timeInMillis
                } else {
                    val alarmDate = Calendar.getInstance().apply {
                        time = Date(alarmTime)
                    }
                    initCalendarAndTimePicker(alarmDate)
                    date_picker.minDate = now.timeInMillis
                }
            } else {
                noteViewModel.getAlarmOfNote(this)
                noteViewModel.alarmOfNoteLiveData.observe(viewLifecycleOwner, Observer {
                    if (it != null) {
                        alarmId = it.alarmId

                        val alarmDate = Calendar.getInstance().apply {
                            time = Date(it.alarmTime)
                        }
                        initCalendarAndTimePicker(alarmDate)
                    } else {
                        initCalendarAndTimePicker(now)
                        date_picker.minDate = now.timeInMillis
                    }
                })
            }
        }

        date_time_set.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v) {
            date_time_set -> {
                val calendar = GregorianCalendar(
                    date_picker.year,
                    date_picker.month,
                    date_picker.dayOfMonth,
                    time_picker.currentHour,
                    time_picker.currentMinute
                )

                val time = calendar.timeInMillis

                if (time > System.currentTimeMillis()) {
                    if (noteId != null && noteId != 0L) {
                        noteViewModel.insertAlarm(
                            AlarmOfNoteEntity(
                                if (alarmId != null) alarmId else null,
                                noteId!!,
                                time
                            )
                        )
                        if (alarmId != null) AlarmManagerUtil.cancelNotification(
                            requireContext(),
                            noteId!!,
                            alarmManager
                        )
                        AlarmManagerUtil.setNotification(
                            requireContext(),
                            noteId!!, alarmManager, time
                        )
                    }

                    if (noteId == 0L) (activity as NoteActivity).setAlarm(time)

                    Toast.makeText(
                        context,
                        R.string.txt_set_notification_successfully,
                        Toast.LENGTH_SHORT
                    ).show()
                    dismiss()
                } else {
                    Toast.makeText(
                        context,
                        getString(R.string.txt_alarm_must_later_now),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun initCalendarAndTimePicker(calendar: Calendar) {
        date_picker.init(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ) { _, _, _, _ ->
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            time_picker.hour = calendar.get(Calendar.HOUR_OF_DAY)
            time_picker.minute = calendar.get(Calendar.MINUTE)
        } else {
            time_picker.currentHour = calendar.get(Calendar.HOUR_OF_DAY)
            time_picker.currentMinute = calendar.get(Calendar.MINUTE)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(noteId: Long?, alarmTime: Long) = SetNotificationDialogFragment().apply {
            arguments = Bundle().apply {
                if (noteId != null) {
                    putLong(Constants.noteId, noteId)
                    putLong(Constants.alarmTime, alarmTime)
                }
            }
        }
    }
}