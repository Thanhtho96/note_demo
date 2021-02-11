package com.tt.noteapplication

import android.app.Application
import com.tt.noteapplication.di.appComponent
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class NoteApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@NoteApp)
            androidLogger()
            modules(appComponent)
        }
    }
}