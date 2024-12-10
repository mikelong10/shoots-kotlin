package com.shoots.shoots_ui.ui.auth

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.shoots.shoots_ui.data.local.DatabaseModule
import com.shoots.shoots_ui.data.remote.NetworkModule
import com.shoots.shoots_ui.data.repository.AuthRepository

class AuthViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val database = DatabaseModule.getDatabase(context)
        val repository = AuthRepository(NetworkModule.apiService, database.userDao())
        return AuthViewModel(repository) as T
    }
}