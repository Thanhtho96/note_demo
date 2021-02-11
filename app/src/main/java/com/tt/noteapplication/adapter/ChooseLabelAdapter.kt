package com.tt.noteapplication.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.recyclerview.widget.RecyclerView
import com.tt.noteapplication.R
import com.tt.noteapplication.model.LabelAssociate
import kotlinx.android.synthetic.main.item_label_checkbox.view.*

class ChooseLabelAdapter(private val listLabelAssociate: List<LabelAssociate>) :
    RecyclerView.Adapter<ChooseLabelAdapter.ViewHolder>() {

    private lateinit var listener: OnItemClickListener

    interface OnItemClickListener {
        fun onCheckBoxCheck(position: Int, cb: CompoundButton, isChecked: Boolean)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    class ViewHolder(itemView: View, listener: OnItemClickListener) :
        RecyclerView.ViewHolder(itemView) {

        fun bindTo(labelEntity: LabelAssociate) {
            itemView.labelTitle.text = labelEntity.labelTitle
            itemView.labelCheckBox.isChecked = labelEntity.isLabelOfNote == 1
        }

        init {
            itemView.labelCheckBox.setOnCheckedChangeListener { cb: CompoundButton, b: Boolean ->
                val adapterPos: Int = adapterPosition
                if (adapterPos != RecyclerView.NO_POSITION) {
                    listener.onCheckBoxCheck(adapterPos, cb, b)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_label_checkbox, parent, false),
            listener
        )
    }

    override fun getItemCount() = listLabelAssociate.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val labelEntity = listLabelAssociate[position]
        holder.bindTo(labelEntity)
    }
}