package com.shoots.shoots_ui

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.*
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import com.shoots.shoots_ui.data.model.User
import com.shoots.shoots_ui.data.repository.AuthRepository
import com.shoots.shoots_ui.ui.auth.AuthState
import com.shoots.shoots_ui.ui.auth.AuthViewModel

class AuthViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: AuthRepository
    private lateinit var viewModel: AuthViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
    }

    @Test
    fun `initial state with user available sets Authenticated`() = runTest {
        val user = User(
            id = 1,
            email = "test@example.com",
            profile_picture = "pic",
            name = "Test User",
            inserted_at = "2023-01-01T00:00:00Z",
            updated_at = null
        )
        coEvery { repository.getUser() } returns user

        viewModel = AuthViewModel(repository)
        advanceUntilIdle()

        assertTrue(viewModel.authState.value is AuthState.Authenticated)
    }

    @Test
    fun `initial state with no user sets NotAuthenticated`() = runTest {
        coEvery { repository.getUser() } returns null

        viewModel = AuthViewModel(repository)
        advanceUntilIdle()

        assertTrue(viewModel.authState.value is AuthState.NotAuthenticated)
    }

    @Test
    fun `initial state error sets Error`() = runTest {
        coEvery { repository.getUser() } throws Exception("Network error")

        viewModel = AuthViewModel(repository)
        advanceUntilIdle()

        val state = viewModel.authState.value
        assertTrue(state is AuthState.Error && state.message.contains("Network error"))
    }

    @Test
    fun `login success sets Authenticated`() = runTest {
        val user = User(
            id = 2,
            email = "login@example.com",
            profile_picture = "pic",
            name = "Login User",
            inserted_at = "2023-01-01T00:00:00Z",
            updated_at = null
        )
        coEvery { repository.getUser() } returns null // initial
        coEvery { repository.login("login@example.com", "password") } returns user

        viewModel = AuthViewModel(repository)
        advanceUntilIdle()

        viewModel.login("login@example.com", "password")
        advanceUntilIdle()

        assertTrue(viewModel.authState.value is AuthState.Authenticated)
    }

    @Test
    fun `login failure sets Error`() = runTest {
        coEvery { repository.getUser() } returns null // initial
        coEvery { repository.login("fail@example.com", "wrong") } throws Exception("Invalid credentials")

        viewModel = AuthViewModel(repository)
        advanceUntilIdle()

        viewModel.login("fail@example.com", "wrong")
        advanceUntilIdle()

        val state = viewModel.authState.value
        assertTrue(state is AuthState.Error && state.message.contains("Invalid credentials"))
    }

    @Test
    fun `register success sets Authenticated`() = runTest {
        val user = User(
            id = 3,
            email = "new@example.com",
            profile_picture = "pic",
            name = "New User",
            inserted_at = "2023-01-01T00:00:00Z",
            updated_at = null
        )
        coEvery { repository.getUser() } returns null // initial
        coEvery { repository.register("new@example.com", "pass", "New User") } returns user

        viewModel = AuthViewModel(repository)
        advanceUntilIdle()

        viewModel.register("new@example.com", "pass", "New User")
        advanceUntilIdle()

        assertTrue(viewModel.authState.value is AuthState.Authenticated)
    }

    @Test
    fun `register failure sets Error`() = runTest {
        coEvery { repository.getUser() } returns null // initial
        coEvery { repository.register("fail@example.com", "pass", "Fail User") } throws Exception("Registration failed")

        viewModel = AuthViewModel(repository)
        advanceUntilIdle()

        viewModel.register("fail@example.com", "pass", "Fail User")
        advanceUntilIdle()

        val state = viewModel.authState.value
        assertTrue(state is AuthState.Error && state.message.contains("Registration failed"))
    }

    @Test
    fun `handleGoogleSignIn success sets Authenticated`() = runTest {
        val user = User(
            id = 4,
            email = "google@example.com",
            profile_picture = "pic",
            name = "Google User",
            inserted_at = "2023-01-01T00:00:00Z",
            updated_at = null
        )
        coEvery { repository.getUser() } returns null // initial
        coEvery { repository.googleAuth("fake_id_token") } returns user

        viewModel = AuthViewModel(repository)
        advanceUntilIdle()

        viewModel.handleGoogleSignIn("fake_id_token")
        advanceUntilIdle()

        assertTrue(viewModel.authState.value is AuthState.Authenticated)
    }

    @Test
    fun `handleGoogleSignIn failure sets Error`() = runTest {
        coEvery { repository.getUser() } returns null // initial
        coEvery { repository.googleAuth("bad_token") } throws Exception("Google sign in failed")

        viewModel = AuthViewModel(repository)
        advanceUntilIdle()

        viewModel.handleGoogleSignIn("bad_token")
        advanceUntilIdle()

        val state = viewModel.authState.value
        assertTrue(state is AuthState.Error && state.message.contains("Google sign in failed"))
    }

    @Test
    fun `logout sets NotAuthenticated`() = runTest {
        // Start authenticated
        val user = User(
            id = 5,
            email = "auth@example.com",
            profile_picture = "pic",
            name = "Auth User",
            inserted_at = "2023-01-01T00:00:00Z",
            updated_at = null
        )
        coEvery { repository.getUser() } returns user
        coEvery { repository.logout() } returns Unit

        viewModel = AuthViewModel(repository)
        advanceUntilIdle()

        assertTrue(viewModel.authState.value is AuthState.Authenticated)

        viewModel.logout()
        advanceUntilIdle()

        coVerify { repository.logout() }
        assertTrue(viewModel.authState.value is AuthState.NotAuthenticated)
    }
}
