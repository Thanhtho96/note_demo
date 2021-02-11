package com.tt.noteapplication.model

import androidx.room.Entity

@Entity(primaryKeys = ["labelId", "noteId"])
data class LabelNoteCrossRef(
    val labelId: Long,
    val noteId: Long
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LabelNoteCrossRef

        if (labelId != other.labelId) return false
        if (noteId != other.noteId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = labelId.hashCode()
        result = 31 * result + noteId.hashCode()
        return result
    }
}