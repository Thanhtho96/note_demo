package com.tt.noteapplication.model

data class LabelAssociate(var labelId: Long, val labelTitle: String, var isLabelOfNote: Int) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LabelAssociate

        if (labelTitle != other.labelTitle) return false

        return true
    }

    override fun hashCode(): Int {
        return labelTitle.hashCode()
    }
}