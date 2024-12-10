package com.shoots.shoots_ui.data.model

// Generic API Response wrapper
data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T
)

// Auth related classes
data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val email: String,
    val password: String,
    val name: String
)

data class AuthData(
    val user: User,
    val accessToken: String,
    val refreshToken: String
)

data class User(
    val id: Int,
    val email: String,
    val profile_picture: String?,
    val name: String,
    val inserted_at: String,
    val updated_at: String?
)

// Type aliases for common response types
typealias LoginResponse = ApiResponse<AuthData>
typealias RegisterResponse = ApiResponse<AuthData>
typealias UserResponse = ApiResponse<User>