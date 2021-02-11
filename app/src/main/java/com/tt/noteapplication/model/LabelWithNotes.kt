package com.tt.noteapplication.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class LabelWithNotes(
    @Embedded val label: LabelEntity?,
    @Relation(
        parentColumn = "labelId",
        entityColumn = "noteId",
        associateBy = Junction(LabelNoteCrossRef::class)
    )

    val notes: List<NoteEntity>
)