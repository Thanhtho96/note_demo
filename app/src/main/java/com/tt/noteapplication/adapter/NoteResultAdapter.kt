package com.tt.noteapplication.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.tt.noteapplication.R
import com.tt.noteapplication.model.NoteEntity
import com.tt.noteapplication.util.Constants
import com.tt.noteapplication.util.DateUtil
import com.tt.noteapplication.util.JSoupUtil
import com.tt.noteapplication.util.NoteDiffUtil
import kotlinx.android.synthetic.main.item_note_search_result.view.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.util.*

class NoteResultAdapter :
    PagedListAdapter<NoteEntity, NoteResultAdapter.ViewHolder>(NoteDiffUtil.DIFF_CALLBACK) {

    private lateinit var listener: OnItemClickListener

    interface OnItemClickListener {
        fun onItemClick(position: Int)
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

        fun bindTo(noteEntity: NoteEntity) {
            itemView.modified_date.text = DateUtil.formatToString(
                Constants.monthDayYear24HourPattern,
                Date(noteEntity.modifiedDate * 1000)
            )

            if (noteEntity.isLocked) {
                itemView.image_view_lock.visibility = View.VISIBLE
                itemView.image_thumbnail_card.visibility = View.GONE
            } else {
                itemView.image_view_lock.visibility = View.GONE
                val img: Element? = Jsoup.parse(noteEntity.content).select("img").first()
                if (img != null) {
                    itemView.image_thumbnail_card.visibility = View.VISIBLE
                    Glide.with(context)
                        .load(img.attr("src"))
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .into(itemView.image_thumbnail)
                } else {
                    itemView.image_thumbnail_card.visibility = View.GONE
                }
            }

            itemView.content.text = if (noteEntity.title.isNullOrEmpty()) {
                if (noteEntity.isLocked) {
                    val sb = StringBuilder(noteEntity.content.length - 1)
                    for (i in noteEntity.content.indices) {
                        sb.append('*')
                    }
                    sb.toString()
                } else {
                    JSoupUtil.replaceBreakAndSpace(noteEntity.content)
                }
            } else {
                noteEntity.title
            }
        }

        init {
            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    listener.onItemClick(adapterPosition)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_note_search_result, parent, false),
            listener,
            parent.context
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val noteEntity = getItem(position)

        noteEntity?.let { holder.bindTo(it) }
    }
}