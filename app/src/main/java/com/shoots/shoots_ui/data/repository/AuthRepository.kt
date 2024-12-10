package com.shoots.shoots_ui.data.repository

import com.shoots.shoots_ui.data.local.UserDao
import com.shoots.shoots_ui.data.local.UserEntity
import com.shoots.shoots_ui.data.model.LoginRequest
import com.shoots.shoots_ui.data.model.RegisterRequest
import com.shoots.shoots_ui.data.model.User
import com.shoots.shoots_ui.data.remote.ApiService
import com.shoots.shoots_ui.data.remote.GoogleAuthRequest

class AuthRepository(
    private val apiService: ApiService,
    private val userDao: UserDao
) {
    suspend fun login(email: String, password: String): User {
        val response = apiService.login(LoginRequest(email, password))
        if (response.success) {
            println("Login response successful, storing user in Room") // Debug log
            val userEntity = UserEntity.fromAuthData(response.data)
            userDao.insertUser(userEntity)
            return response.data.user
        } else {
            throw Exception(response.message)
        }
    }

    suspend fun register(email: String, password: String, name: String): User {
        val response = apiService.register(RegisterRequest(email, password, name))
        if (response.success) {
            println("Registration response successful, storing user in Room") // Debug log
            val userEntity = UserEntity.fromAuthData(response.data)
            userDao.insertUser(userEntity)
            return response.data.user
        } else {
            throw Exception(response.message)
        }
    }

    suspend fun googleAuth(idToken: String): User {
        val response = apiService.googleAuth(GoogleAuthRequest(idToken))
        if (response.success) {
            println("Google auth response successful, storing user in Room") // Debug log
            val userEntity = UserEntity.fromAuthData(response.data)
            userDao.insertUser(userEntity)
            return response.data.user
        } else {
            throw Exception(response.message)
        }
    }

    suspend fun getUser(): User? {
        val userEntity = userDao.getUser()
        println("Getting user from Room: $userEntity") // Debug log
        return userEntity?.toUser()
    }

    suspend fun logout() {
        println("Logging out, clearing user from Room") // Debug log
        userDao.deleteUser()
    }
}