package com.tt.noteapplication.util

import androidx.recyclerview.widget.DiffUtil
import com.tt.noteapplication.model.NoteEntity

object NoteDiffUtil {
    val DIFF_CALLBACK = object :
        DiffUtil.ItemCallback<NoteEntity>() {
        // PlaceEntity details may have changed if reloaded from the database,
        // but ID is fixed.
        override fun areItemsTheSame(oldItem: NoteEntity, newItem: NoteEntity) =
            oldItem.noteId == newItem.noteId &&
                    oldItem.title == newItem.title &&
                    oldItem.content == newItem.content &&
                    oldItem.isLocked == newItem.isLocked

        override fun areContentsTheSame(oldItem: NoteEntity, newItem: NoteEntity) =
            oldItem == newItem
    }
}