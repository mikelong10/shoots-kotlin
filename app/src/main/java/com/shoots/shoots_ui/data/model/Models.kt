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

data class Group(
    val id: Int,
    val screen_time_goal: Int,
    val code: String,
    val stake: Double,
    val name: String,
    val inserted_at: String,
    val updated_at: String?
)

data class CreateGroupRequest(
    val name: String,
    val screen_time_goal: Int,
    val stake: Double
)

data class JoinGroupRequest(
    val code: String
)

data class ScreenTime(
    val id: Int,
    val userId: Int,
    val submitted_time: Int,
    val inserted_at: String,
)

data class CreateScreenTimeRequest(
    val screen_time: Int,
)

// Type aliases for common response types
typealias LoginResponse = ApiResponse<AuthData>
typealias RegisterResponse = ApiResponse<AuthData>
typealias UserResponse = ApiResponse<User>
typealias GroupResponse = ApiResponse<Group>
typealias GroupsResponse = ApiResponse<List<Group>>
typealias ScreenTimeResponse = ApiResponse<ScreenTime>