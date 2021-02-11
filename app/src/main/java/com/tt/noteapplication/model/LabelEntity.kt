package com.tt.noteapplication.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "label")
data class LabelEntity(
    @PrimaryKey(autoGenerate = true)
    var labelId: Long? = null,
    var title: String,
    val createDate: Long,
    var modifiedDate: Long
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LabelEntity

        if (title != other.title) return false

        return true
    }

    override fun hashCode(): Int {
        return title.hashCode()
    }
}