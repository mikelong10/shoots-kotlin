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
            _authState.value = AuthState.Loading
            try {
                val user = userDao.getUser()
                if (user != null) {
                    _authState.value = AuthState.Authenticated(user.toUser())
                } else {
                    _authState.value = AuthState.NotAuthenticated
                }
            } catch (e: Exception) {
                _authState.value = AuthState.NotAuthenticated
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading
                val response = apiService.login(LoginRequest(email, password))
                if (response.success) {
                    println(response)
                    val userEntity = UserEntity.fromAuthData(response.data)
                    userDao.insertUser(userEntity)
                    _authState.value = AuthState.Authenticated(response.data.user)
                } else {
                    _authState.value = AuthState.Error(response.message)
                }
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
                if (response.success) {
                    val userEntity = UserEntity.fromAuthData(response.data)
                    userDao.insertUser(userEntity)
                    _authState.value = AuthState.Authenticated(response.data.user)
                } else {
                    _authState.value = AuthState.Error(response.message)
                }
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
                if (response.success) {
                    val userEntity = UserEntity.fromAuthData(response.data)
                    userDao.insertUser(userEntity)
                    _authState.value = AuthState.Authenticated(response.data.user)
                } else {
                    _authState.value = AuthState.Error(response.message)
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    private suspend fun saveUserLocally(response: LoginResponse) {
        userDao.insertUser(
            UserEntity(
                id = response.data.user.id,
                email = response.data.user.email,
                name = response.data.user.name,
                profilePicture = response.data.user.profile_picture,
                accessToken = response.data.accessToken,
                refreshToken = response.data.refreshToken,
                insertedAt = response.data.user.inserted_at,
                updatedAt = response.data.user.updated_at
            )
        )
    }

    fun updateAuthState(state: AuthState) {
        _authState.value = state
    }

    fun logout() {
        viewModelScope.launch {
            userDao.deleteUser()
            _authState.value = AuthState.NotAuthenticated
        }
    }
}