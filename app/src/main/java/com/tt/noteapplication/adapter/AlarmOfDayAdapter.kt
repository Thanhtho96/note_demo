package com.tt.noteapplication.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tt.noteapplication.R
import com.tt.noteapplication.model.AlarmAndNote
import com.tt.noteapplication.util.Constants
import com.tt.noteapplication.util.DateUtil
import com.tt.noteapplication.util.JSoupUtil
import kotlinx.android.synthetic.main.item_alarm_of_day.view.*
import java.util.*

class AlarmOfDayAdapter(private val listAlarm: List<AlarmAndNote>) :
    RecyclerView.Adapter<AlarmOfDayAdapter.ViewHolder>() {

    private lateinit var listener: OnItemClickListener

    interface OnItemClickListener {
        fun onItemClick(position: Int)
        fun onLongItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    class ViewHolder(
        itemView: View,
        listener: OnItemClickListener,
        val context: Context
    ) :
        RecyclerView.ViewHolder(itemView) {

        fun bindTo(alarmAndNote: AlarmAndNote) {
            itemView.alarm_time.text = DateUtil.formatToString(
                Constants.hourMinutePattern,
                Date(alarmAndNote.alarmOfNoteEntity.alarmTime)
            )
            itemView.title.text =
                alarmAndNote.note.title ?: context.getString(R.string.txt_no_title)
            itemView.content.text = if (alarmAndNote.note.isLocked) {
                val sb = StringBuilder(alarmAndNote.note.content.length - 1)
                for (i in alarmAndNote.note.content.indices) {
                    sb.append('*')
                }
                sb.toString()
            } else {
                JSoupUtil.replaceBreakAndSpace(alarmAndNote.note.content)
            }
        }

        init {
            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    listener.onItemClick(adapterPosition)
                }
            }

            itemView.setOnLongClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    listener.onLongItemClick(adapterPosition)
                }
                return@setOnLongClickListener true
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_alarm_of_day, parent, false),
            listener,
            parent.context
        )
    }

    override fun getItemCount() = listAlarm.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val alarmAndNote = listAlarm[position]
        holder.bindTo(alarmAndNote)
    }
}