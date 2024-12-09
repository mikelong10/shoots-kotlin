package com.shoots.shoots_ui.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.shoots.shoots_ui.data.model.User

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: Int,
    val email: String,
    val name: String,
    val profilePicture: String?,
    val accessToken: String,
    val insertedAt: String,
    val updatedAt: String?
) {
    fun toUser(): User {
        return User(
            id = id,
            email = email,
            name = name,
            profile_picture = profilePicture,
            inserted_at = insertedAt,
            updated_at = updatedAt
        )
    }

    companion object {
        fun fromUser(user: User, token: String): UserEntity {
            return UserEntity(
                id = user.id,
                email = user.email,
                name = user.name,
                profilePicture = user.profile_picture,
                accessToken = token,
                insertedAt = user.inserted_at,
                updatedAt = user.updated_at
            )
        }
    }
}