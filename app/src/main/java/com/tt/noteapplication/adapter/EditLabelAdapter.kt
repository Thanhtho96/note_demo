package com.tt.noteapplication.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tt.noteapplication.R
import com.tt.noteapplication.model.LabelEntity
import com.tt.noteapplication.util.KeyboardUtil
import kotlinx.android.synthetic.main.item_label_edit.view.*

class EditLabelAdapter(private val listLabel: List<LabelEntity>) :
    RecyclerView.Adapter<EditLabelAdapter.ViewHolder>() {

    private lateinit var listener: OnItemClickListener

    interface OnItemClickListener {
        fun onDeleteClick(position: Int, isAppear: Boolean)
        fun onCheckClick(position: Int, newTitle: String, isAppear: Boolean)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    class ViewHolder(itemView: View, listener: OnItemClickListener) :
        RecyclerView.ViewHolder(itemView) {
        private var isEditMode = false

        init {
            itemView.image_delete.apply {
                setOnClickListener {
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        listener.onDeleteClick(adapterPosition, isEditMode)
                    }
                }
            }

            itemView.image_edit.apply {
                setOnClickListener {
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        if (isEditMode && itemView.edit_text_add_label.text.toString()
                                .isNotBlank()
                        ) {
                            isEditMode = false
                            itemView.image_delete.setImageResource(R.drawable.ic_label)
                            setImageResource(R.drawable.ic_edit)
                            itemView.edit_text_add_label.isFocusableInTouchMode = false
                            itemView.edit_text_add_label.clearFocus()
                            KeyboardUtil.closeSoftKeyboard(context, itemView.edit_text_add_label)
                        } else {
                            isEditMode = true
                            itemView.image_delete.setImageResource(R.drawable.ic_delete)
                            setImageResource(R.drawable.ic_check)
                            itemView.edit_text_add_label.isFocusableInTouchMode = true
                            itemView.edit_text_add_label.setSelection(itemView.edit_text_add_label.text.length)
                            itemView.edit_text_add_label.requestFocus()
                            KeyboardUtil.showSoftKeyboard(context, itemView.edit_text_add_label)
                        }
                        listener.onCheckClick(
                            adapterPosition,
                            itemView.edit_text_add_label.text.toString(),
                            isEditMode
                        )
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_label_edit, parent, false),
            listener
        )
    }

    override fun getItemCount() = listLabel.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.edit_text_add_label.setText(listLabel[position].title)
    }
}