package com.shoots.shoots_ui.data.remote

import com.shoots.shoots_ui.data.model.LoginRequest
import com.shoots.shoots_ui.data.model.LoginResponse
import com.shoots.shoots_ui.data.model.RegisterRequest
import com.shoots.shoots_ui.data.model.RegisterResponse
import com.shoots.shoots_ui.data.model.UserResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

data class GoogleAuthRequest(
    val idToken: String
)

interface ApiService {
    @POST("auth/google")
    suspend fun googleAuth(@Body request: GoogleAuthRequest): LoginResponse

    @POST("auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): LoginResponse

    @POST("auth/register")
    suspend fun register(@Body registerRequest: RegisterRequest): RegisterResponse

    @GET("user")
    suspend fun getSelf(): UserResponse
}