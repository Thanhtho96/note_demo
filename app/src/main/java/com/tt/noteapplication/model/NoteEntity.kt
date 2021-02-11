package com.tt.noteapplication.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "note")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    var noteId: Long? = null,
    val title: String?,
    val content: String,
    val createDate: Long,
    val modifiedDate: Long,
    val bgColorId: Int,
    var isLocked: Boolean = false
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NoteEntity

        if (noteId != other.noteId) return false
        if (title != other.title) return false
        if (content != other.content) return false
        if (isLocked != other.isLocked) return false

        return true
    }

    override fun hashCode(): Int {
        var result = noteId?.hashCode() ?: 0
        result = 31 * result + (title?.hashCode() ?: 0)
        result = 31 * result + content.hashCode()
        result = 31 * result + isLocked.hashCode()
        return result
    }
}