package com.tt.noteapplication.util

object Constants {
    const val alarmTime = "alarmTime"
    const val font_sharePref = "font_setting"
    const val chooseFontDialog = "choose_font_dialog"
    const val theme_sharePref = "theme_setting"
    const val chooseThemeDialog = "choose_theme_dialog"
    const val notificationDialog = "notification_dialog"
    const val attachmentDialog = "attachment_dialog"
    const val monthDayYear24HourPattern = "MM/dd/yyyy HH:mm"
    const val monthDayYearPattern = "MM/dd/yyyy"
    const val hourMinutePattern = "HH:mm"
    const val dateTimeSecondFileNamePattern = "yyyyMMdd_HHmmss"
    const val fileProviderExtension = ".fileprovider"
    const val noteId = "noteId"
    const val labelId = "labelId"
    const val lockPattern = "lockPattern"
    const val isUnlocked = "isUnlocked"
    const val fragmentMainNote = "main_note_fragment"
    const val fragmentNotification = "notification_fragment"

    enum class Tab {
        NOTE, PRIVACY, LABEL
    }

    enum class Theme {
        LIGHT, DARK, SYSTEM_DEFAULT
    }

    enum class Font {
        SMALL, NORMAL, LARGE
    }

    fun setImageStyleWebView(fileName: String): String {
        return "<img src=\"$fileName\" style='width:100%'/><br><br>"
    }
}