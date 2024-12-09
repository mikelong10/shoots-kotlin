package com.shoots.shoots_ui

import android.app.Application
import com.shoots.shoots_ui.data.remote.NetworkModule

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        NetworkModule.initialize(this)
    }
}