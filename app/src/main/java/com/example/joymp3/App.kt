package com.example.joymp3

import android.app.Application
import com.example.joymp3.ui.fragments.MusicPlayerManagerSer
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {

    val musicPlayerManager: MusicPlayerManagerSer by lazy {
        MusicPlayerManagerSer()
    }

    companion object{
        lateinit var instance: App
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}