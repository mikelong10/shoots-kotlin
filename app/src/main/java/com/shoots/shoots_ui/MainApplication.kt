package com.shoots.shoots_ui

import android.app.Application
import androidx.work.Configuration
import com.shoots.shoots_ui.data.local.DatabaseModule
import com.shoots.shoots_ui.data.remote.NetworkModule
import com.shoots.shoots_ui.data.repository.GroupRepository
import com.shoots.shoots_ui.data.worker.GroupSyncWorker

class MainApplication : Application(), Configuration.Provider {
    override fun onCreate() {
        super.onCreate()
        
        // Start periodic sync after WorkManager is initialized via Configuration.Provider
        GroupSyncWorker.startPeriodicSync(this)
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(
                GroupSyncWorker.Factory(
                    GroupRepository(NetworkModule.apiService),
                    DatabaseModule.getDatabase(this).groupDao()
                )
            )
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()
}