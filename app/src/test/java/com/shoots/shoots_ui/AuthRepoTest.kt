package com.shoots.shoots_ui

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import com.shoots.shoots_ui.data.model.User
import com.shoots.shoots_ui.data.remote.ApiService
import com.shoots.shoots_ui.data.local.UserDao
import com.shoots.shoots_ui.data.local.UserEntity
import com.shoots.shoots_ui.data.model.ApiResponse
import com.shoots.shoots_ui.data.model.AuthData
import com.shoots.shoots_ui.data.model.LoginRequest
import com.shoots.shoots_ui.data.repository.AuthRepository

class AuthRepoTest {
    private val apiService = mockk<ApiService>()
    private val userDao = mockk<UserDao>()
    private val repository = AuthRepository(apiService, userDao)

    @Test
    fun `getUser returns user from local or remote`() = runTest {
        // Mock a UserEntity from the database
        val userEntity = UserEntity(
            id = 1,
            email = "test@example.com",
            name = "Test",
            profilePicture = "pic",
            accessToken = "access",
            refreshToken = "refresh",
            insertedAt = "2023-01-01T00:00:00Z",
            updatedAt = "2023-01-01T00:00:00Z"
        )

        // Mock userDao to return userEntity
        coEvery { userDao.getUser() } returns userEntity

        // The repository should convert UserEntity to User internally
        val result = repository.getUser()

        // Create the expected User object that matches what repository would produce
        val expectedUser = User(
            id = 1,
            name = "Test",
            email = "test@example.com",
            profile_picture = "pic",
            inserted_at = "2023-01-01T00:00:00Z",
            updated_at = "2023-01-01T00:00:00Z"
        )

        assertEquals(expectedUser, result)
    }

    @Test
    fun `login returns user on success`() = runTest {
        val user = User(
            id = 1,
            name = "Test",
            email = "test@example.com",
            profile_picture = "pic",
            inserted_at = "2023-01-01T00:00:00Z",
            updated_at = "2023-01-01T00:00:00Z"
        )

        // Create AuthData with the user and tokens
        val authData = AuthData(
            user = user,
            accessToken = "fake_access_token",
            refreshToken = "fake_refresh_token"
        )

        // Create an ApiResponse containing AuthData
        val response = ApiResponse(
            data = authData,
            success = true,
            message = ""
        )

        // Mock the API service to return the ApiResponse<AuthData>
        coEvery { apiService.login(LoginRequest("test@example.com", "password")) } returns response

        // The repository will convert AuthData into User and store tokens as needed
        val userEntity = UserEntity(
            id = user.id,
            email = user.email,
            name = user.name,
            profilePicture = user.profile_picture,
            accessToken = "fake_access_token",
            refreshToken = "fake_refresh_token",
            insertedAt = user.inserted_at,
            updatedAt = user.updated_at
        )

        // Mock the DAO insert operation
        coEvery { userDao.insertUser(userEntity) } returns Unit

        // Call repository login
        val result = repository.login("test@example.com", "password")

        // Verify that the result matches the expected user
        assertEquals(user, result)
    }

}
