package com.shoots.shoots_ui.data.local

import android.content.Context
import androidx.room.Room

object DatabaseModule {
    private var database: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return database ?: synchronized(this) {
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "app_database"
            )
                .fallbackToDestructiveMigration()
                .build().also { database = it }
        }
    }
}