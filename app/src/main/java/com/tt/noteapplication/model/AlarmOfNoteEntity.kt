package com.tt.noteapplication.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "note_with_alarm"/*,
    foreignKeys = [ForeignKey(
        entity = NoteEntity::class,
        parentColumns = ["noteId"],
        childColumns = ["noteId"],
        onUpdate = ForeignKey.NO_ACTION,
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["noteId"])]*/
)
data class AlarmOfNoteEntity(
    @PrimaryKey(autoGenerate = true)
    var alarmId: Long? = null,
    val noteId: Long,
    val alarmTime: Long
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AlarmOfNoteEntity

        if (alarmId != other.alarmId) return false
        if (noteId != other.noteId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = alarmId?.hashCode() ?: 0
        result = 31 * result + noteId.hashCode()
        return result
    }
}