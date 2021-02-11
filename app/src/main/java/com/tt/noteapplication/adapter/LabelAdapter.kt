package com.tt.noteapplication.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tt.noteapplication.R
import com.tt.noteapplication.model.LabelEntity
import kotlinx.android.synthetic.main.item_label.view.*

class LabelAdapter(private val listLabel: List<LabelEntity>) :
    RecyclerView.Adapter<LabelAdapter.ViewHolder>() {

    private lateinit var listener: OnItemClickListener

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    class ViewHolder(itemView: View, listener: OnItemClickListener) :
        RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {
                val adapterPos: Int = adapterPosition
                if (adapterPos != RecyclerView.NO_POSITION) {
                    listener.onItemClick(adapterPos)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_label, parent, false),
            listener
        )
    }

    override fun getItemCount() = listLabel.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.labelTitle.text = listLabel[position].title
    }
}