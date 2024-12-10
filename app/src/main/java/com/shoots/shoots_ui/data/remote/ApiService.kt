package com.shoots.shoots_ui.data.remote

import com.shoots.shoots_ui.data.model.ApiResponse
import com.shoots.shoots_ui.data.model.CreateGroupRequest
import com.shoots.shoots_ui.data.model.CreateScreenTimeRequest
import com.shoots.shoots_ui.data.model.GroupResponse
import com.shoots.shoots_ui.data.model.GroupsResponse
import com.shoots.shoots_ui.data.model.JoinGroupRequest
import com.shoots.shoots_ui.data.model.LoginRequest
import com.shoots.shoots_ui.data.model.LoginResponse
import com.shoots.shoots_ui.data.model.RegisterRequest
import com.shoots.shoots_ui.data.model.RegisterResponse
import com.shoots.shoots_ui.data.model.ScreenTimeResponse
import com.shoots.shoots_ui.data.model.UserResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

data class GoogleAuthRequest(
    val idToken: String
)

data class RefreshRequest(
    val refreshToken: String
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

    @POST("token/refresh")
    fun refresh(@Body request: RefreshRequest): Call<LoginResponse>

    // Group endpoints
    @GET("groups")
    suspend fun listGroups(): GroupsResponse

    // Group endpoints
    @GET("groups?self=true")
    suspend fun listMyGroups(): GroupsResponse

    @GET("groups/{id}")
    suspend fun getGroup(@Path("id") id: Int): GroupResponse

    @POST("groups")
    suspend fun createGroup(@Body request: CreateGroupRequest): GroupResponse

    @PUT("groups/{groupId}/invite")
    suspend fun createInvite(@Path("groupId") groupId: Int): ApiResponse<String>

    @PUT("groups/join")
    suspend fun joinGroup(@Body request: JoinGroupRequest): GroupResponse

    @POST("screenTime")
    suspend fun enterScreenTime(@Body request: CreateScreenTimeRequest): ScreenTimeResponse
}