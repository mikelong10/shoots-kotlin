package com.shoots.shoots_ui.data.model

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val email: String,
    val password: String,
    val name: String
)

data class LoginResponse(
    val user: User,
    val token: String
)

data class User(
    val id: Int,
    val email: String,
    val profile_picture: String?,
    val name: String,
    val inserted_at: String,
    val updated_at: String?
)