package com.tt.noteapplication.di

import android.app.Application
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.tt.noteapplication.viewmodel.NoteViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val appComponent = module {
    single { providesSharedPreferences(get()) }
    viewModel { NoteViewModel(get()) }
}


fun providesSharedPreferences(application: Application): SharedPreferences {
    return PreferenceManager.getDefaultSharedPreferences(application)
}

