package com.shoots.shoots_ui.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shoots.shoots_ui.data.local.UserDao
import com.shoots.shoots_ui.data.local.UserEntity
import com.shoots.shoots_ui.data.model.LoginRequest
import com.shoots.shoots_ui.data.model.LoginResponse
import com.shoots.shoots_ui.data.model.RegisterRequest
import com.shoots.shoots_ui.data.model.User
import com.shoots.shoots_ui.data.remote.ApiService
import com.shoots.shoots_ui.data.remote.GoogleAuthRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Initial : AuthState()
    object Loading : AuthState()
    data class Error(val message: String) : AuthState()
    data class Authenticated(val user: User) : AuthState()
    object NotAuthenticated : AuthState()
}

class AuthViewModel(
    private val apiService: ApiService,
    private val userDao: UserDao
) : ViewModel() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState: StateFlow<AuthState> = _authState

    init {
        checkLocalUser()
    }

    private fun checkLocalUser() {
        viewModelScope.launch {
            try {
                val localUser = userDao.getUser()
                if (localUser != null) {
                    // Convert UserEntity to User
                    _authState.value = AuthState.Authenticated(
                        User(
                            id = localUser.id,
                            email = localUser.email,
                            name = localUser.name,
                            profile_picture = localUser.profilePicture,
                            inserted_at = localUser.insertedAt,
                            updated_at = localUser.updatedAt
                        )
                    )
                } else {
                    _authState.value = AuthState.NotAuthenticated
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Error checking local user")
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading
                val response = apiService.login(LoginRequest(email, password))
                saveUserLocally(response)
                _authState.value = AuthState.Authenticated(response.user)
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun register(email: String, password: String, name: String) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading
                val response = apiService.register(RegisterRequest(email, password, name))
                saveUserLocally(response)
                _authState.value = AuthState.Authenticated(response.user)
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun handleGoogleSignIn(idToken: String) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading
                val response = apiService.googleAuth(GoogleAuthRequest(idToken))
                saveUserLocally(response)
                _authState.value = AuthState.Authenticated(response.user)
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Google Sign-In failed")
            }
        }
    }

    private suspend fun saveUserLocally(response: LoginResponse) {
        userDao.insertUser(
            UserEntity(
                id = response.user.id,
                email = response.user.email,
                name = response.user.name,
                profilePicture = response.user.profile_picture,
                accessToken = response.token,
                insertedAt = response.user.inserted_at,
                updatedAt = response.user.updated_at
            )
        )
    }

    fun logout() {
        viewModelScope.launch {
            userDao.deleteUser()
            _authState.value = AuthState.NotAuthenticated
        }
    }

    fun fetchUserData() {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading
                val user = apiService.getSelf()
                _authState.value = AuthState.Authenticated(user)
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }
}