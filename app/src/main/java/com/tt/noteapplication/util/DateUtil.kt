package com.tt.noteapplication.util

import java.text.SimpleDateFormat
import java.util.*

object DateUtil {
    fun formatToString(pattern: String, date: Date): String =
        SimpleDateFormat(pattern, Locale.getDefault()).format(date)

    fun getCurrentTimeInMillis() = System.currentTimeMillis() / 1000
}