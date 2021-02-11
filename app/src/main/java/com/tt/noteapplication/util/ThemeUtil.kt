package com.tt.noteapplication.util

import androidx.appcompat.app.AppCompatDelegate

object ThemeUtil {
    fun changeDefaultNightMode(themeSetting: String) {
        AppCompatDelegate.setDefaultNightMode(
            when (Constants.Theme.valueOf(themeSetting)) {
                Constants.Theme.LIGHT -> {
                    AppCompatDelegate.MODE_NIGHT_NO
                }
                Constants.Theme.DARK -> {
                    AppCompatDelegate.MODE_NIGHT_YES
                }
                Constants.Theme.SYSTEM_DEFAULT -> {
                    AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                }
            }
        )
    }
}