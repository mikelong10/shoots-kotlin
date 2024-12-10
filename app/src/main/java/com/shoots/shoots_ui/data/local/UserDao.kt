package com.shoots.shoots_ui.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {
    @Query("SELECT * FROM users LIMIT 1")
    suspend fun getUser(): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("DELETE FROM users")
    suspend fun deleteUser()

    @Query("SELECT accessToken FROM users LIMIT 1")
    suspend fun getAccessToken(): String?

    @Query("SELECT refreshToken FROM users LIMIT 1")
    suspend fun getRefreshToken(): String?

    @Query("UPDATE users SET accessToken = :token WHERE id = (SELECT id FROM users LIMIT 1)")
    suspend fun updateAccessToken(token: String)

    @Query("UPDATE users SET refreshToken = :token WHERE id = (SELECT id FROM users LIMIT 1)")
    suspend fun updateRefreshToken(token: String)
}