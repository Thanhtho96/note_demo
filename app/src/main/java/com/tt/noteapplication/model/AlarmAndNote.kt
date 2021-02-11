package com.tt.noteapplication.model

import androidx.room.Embedded
import androidx.room.Relation

data class AlarmAndNote(
    @Embedded val alarmOfNoteEntity: AlarmOfNoteEntity,
    @Relation(
        parentColumn = "noteId",
        entityColumn = "noteId"
    )
    val note: NoteEntity
)