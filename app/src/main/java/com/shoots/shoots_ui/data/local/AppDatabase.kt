package com.shoots.shoots_ui.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.shoots.shoots_ui.data.model.GroupEntity

@Database(
    entities = [UserEntity::class, GroupEntity::class],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun groupDao(): GroupDao
}