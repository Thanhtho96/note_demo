package com.tt.noteapplication.ui

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.prolificinteractive.materialcalendarview.*
import com.tt.noteapplication.R
import com.tt.noteapplication.adapter.AlarmOfDayAdapter
import com.tt.noteapplication.base.BaseFragment
import com.tt.noteapplication.model.AlarmAndNote
import com.tt.noteapplication.util.Constants
import com.tt.noteapplication.util.DateUtil
import com.tt.noteapplication.viewmodel.NoteViewModel
import kotlinx.android.synthetic.main.fragment_notification.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.*
import kotlin.collections.ArrayList

private const val OPEN_LOCK_NOTE_REQUEST_CODE = 77

class NotificationFragment : BaseFragment<NoteViewModel>(), OnDateSelectedListener {

    private val noteViewModel by sharedViewModel<NoteViewModel>()
    private lateinit var alarmOfDayAdapter: AlarmOfDayAdapter
    private val listAlarmEntity: MutableList<AlarmAndNote> = ArrayList()
    private var noteRequestId = 0L
    private var selectedDay: Long? = null
    private var nextSelectedDay: Long? = null

    override fun layoutRes() = R.layout.fragment_notification

    override fun viewModelClass() = NoteViewModel::class.java

    override fun handleViewState() {
        TODO("Not yet implemented")
    }

    override fun initView() {
        noteViewModel.getAllAlarm()
        noteViewModel.alarmLiveData.observe(viewLifecycleOwner, Observer {
            if (selectedDay != null && nextSelectedDay != null) {
                noteViewModel.getAllAlarmInDay(selectedDay!!, nextSelectedDay!!)
            }
            calendarView.removeDecorators()
            for (i in it) {
                val calendar = Calendar.getInstance().apply {
                    time = Date(i.alarmTime)
                }
                calendarView.addDecorator(
                    CurrentDayDecorator(
                        requireActivity(),
                        CalendarDay.from(
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH) + 1,
                            calendar.get(Calendar.DAY_OF_MONTH)
                        )
                    )
                )
            }

        })
        generateNoteResultRecyclerView(requireContext())
    }

    override fun initData() {
        calendarView.setOnDateChangedListener(this)
        noteViewModel.alarmInDayLiveData.observe(viewLifecycleOwner, Observer {
            if (selectedDay != null && nextSelectedDay != null) {
                select_date.text =
                    DateUtil.formatToString(Constants.monthDayYearPattern, Date(nextSelectedDay!!))
            }
            listAlarmEntity.clear()
            listAlarmEntity.addAll(it)
            alarmOfDayAdapter.notifyDataSetChanged()
        })
        val calendar = Calendar.getInstance().apply {
            time = Date(System.currentTimeMillis())
        }
        calendarView.selectedDate = CalendarDay.from(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH) + 1,
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    private fun generateNoteResultRecyclerView(context: Context) {
        alarmOfDayAdapter = AlarmOfDayAdapter(listAlarmEntity)
        alarmOfDayAdapter.setOnItemClickListener(object : AlarmOfDayAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val note = listAlarmEntity[position].note
                if (note.isLocked) {
                    noteRequestId = note.noteId!!
                    startActivityForResult(
                        Intent(context, PrivacyActivity::class.java),
                        OPEN_LOCK_NOTE_REQUEST_CODE
                    )
                } else
                    startActivity(
                        Intent(context, NoteActivity::class.java).apply {
                            putExtra(Constants.noteId, note.noteId)
                        })
            }

            override fun onLongItemClick(position: Int) {
                AlertDialog.Builder(context)
                    .setMessage(R.string.txt_want_to_delete_alarm)
                    .setPositiveButton(
                        R.string.txt_yes
                    ) { _, _ ->
                        viewModel.deleteAlarm(listAlarmEntity[position].alarmOfNoteEntity)
                        listAlarmEntity.removeAt(position)
                        alarmOfDayAdapter.notifyItemRemoved(position)
                    }
                    .setNegativeButton(R.string.txt_cancel) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .setCancelable(false)
                    .show()
            }
        })
        val layoutManager = LinearLayoutManager(context)
        recycler_view_note.layoutManager = layoutManager
        recycler_view_note.itemAnimator = DefaultItemAnimator()
        recycler_view_note.adapter = alarmOfDayAdapter
        recycler_view_note.addItemDecoration(
            DividerItemDecoration(
                context,
                layoutManager.orientation
            ).apply {
                setDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.item_divider_silver
                    )!!
                )
            }
        )
    }

    override fun onDateSelected(
        widget: MaterialCalendarView,
        date: CalendarDay,
        selected: Boolean
    ) {
        selectedDay = date.date.toEpochDay() * 86398635
        nextSelectedDay = date.date.plusDays(1).toEpochDay() * 86398635
        noteViewModel.getAllAlarmInDay(selectedDay!!, nextSelectedDay!!)
    }

    companion object {
        @JvmStatic
        fun newInstance() = NotificationFragment()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                OPEN_LOCK_NOTE_REQUEST_CODE -> {
                    startActivity(
                        Intent(context, NoteActivity::class.java).apply {
                            putExtra(Constants.noteId, noteRequestId)
                        })
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}

class CurrentDayDecorator(context: Activity, currentDay: CalendarDay) : DayViewDecorator {
    private val drawable: Drawable = context.getDrawable(R.drawable.ic_bell)!!
    private var myDay = currentDay
    override fun shouldDecorate(day: CalendarDay): Boolean {
        return day == myDay
    }

    override fun decorate(view: DayViewFacade) {
        view.setSelectionDrawable(drawable)
    }
}