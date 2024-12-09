package com.shoots.shoots_ui.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.shoots.shoots_ui.data.model.AuthData
import com.shoots.shoots_ui.data.model.User

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: Int,
    val email: String,
    val name: String,
    val profilePicture: String?,
    val accessToken: String,
    val refreshToken: String,
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
        fun fromAuthData(authData: AuthData): UserEntity {
            return UserEntity(
                id = authData.user.id,
                email = authData.user.email,
                name = authData.user.name,
                profilePicture = authData.user.profile_picture,
                accessToken = authData.accessToken,
                refreshToken = authData.refreshToken,
                insertedAt = authData.user.inserted_at,
                updatedAt = authData.user.updated_at
            )
        }
    }
}