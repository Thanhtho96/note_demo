package com.tt.noteapplication.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.daimajia.swipe.SwipeLayout
import com.tt.noteapplication.R
import com.tt.noteapplication.model.NoteEntity
import com.tt.noteapplication.util.Constants
import com.tt.noteapplication.util.DateUtil
import com.tt.noteapplication.util.JSoupUtil
import com.tt.noteapplication.util.NoteDiffUtil
import kotlinx.android.synthetic.main.item_note_grid_layout.view.*
import java.util.*

class GridSwipeNoteAdapter :
    PagedListAdapter<NoteEntity, GridSwipeNoteAdapter.NoteViewHolder>(NoteDiffUtil.DIFF_CALLBACK) {

    private lateinit var listener: OnItemClickListener

    interface OnItemClickListener {
        fun onItemClick(position: Int)
        fun onDeleteClick(position: Int)
        fun onLockClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    class NoteViewHolder(
        itemView: View,
        private val context: Context,
        listener: OnItemClickListener
    ) :
        RecyclerView.ViewHolder(itemView) {

        fun bindTo(noteEntity: NoteEntity) {
            itemView.modified_date.text = DateUtil.formatToString(
                Constants.monthDayYear24HourPattern,
                Date(noteEntity.modifiedDate * 1000)
            )

            itemView.title.text = noteEntity.title ?: context.getString(R.string.txt_no_title)

            itemView.content.text = if (noteEntity.isLocked) {
                itemView.image_view_lock.visibility = View.VISIBLE
                val sb = StringBuilder(noteEntity.content.length - 1)
                for (i in noteEntity.content.indices) {
                    sb.append('*')
                }
                sb.toString()
            } else {
                itemView.image_view_lock.visibility = View.GONE
                JSoupUtil.replaceBreakAndSpace(noteEntity.content)
            }
        }

        init {
            itemView.swipeLayoutNote.isClickToClose = true

            itemView.linearLayoutNote.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    listener.onItemClick(adapterPosition)
                }
            }
            itemView.delete.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    listener.onDeleteClick(adapterPosition)
                }
            }
            itemView.lock.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    listener.onLockClick(adapterPosition)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        return NoteViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_note_grid_layout, parent, false),
            parent.context,
            listener
        )
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val noteEntity = getItem(position)

        holder.itemView.swipeLayoutNote.showMode = SwipeLayout.ShowMode.LayDown

        noteEntity?.let { holder.bindTo(it) }
    }
}