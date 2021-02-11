package com.tt.noteapplication.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class NoteWithLabels(
    @Embedded val note: NoteEntity,
    @Relation(
        parentColumn = "noteId",
        entityColumn = "labelId",
        associateBy = Junction(LabelNoteCrossRef::class)
    )

    val labels: List<LabelEntity>
)