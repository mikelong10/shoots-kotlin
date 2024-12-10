package com.shoots.shoots_ui.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "groups")
data class GroupEntity(
    @PrimaryKey
    val id: Int,
    val screenTimeGoal: Int,
    val code: String,
    val stake: Double,
    val name: String,
    val insertedAt: String,
    val updatedAt: String?
) 