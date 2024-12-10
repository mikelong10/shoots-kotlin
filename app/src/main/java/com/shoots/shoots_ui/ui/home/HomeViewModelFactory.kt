package com.shoots.shoots_ui.ui.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.shoots.shoots_ui.data.remote.NetworkModule
import com.shoots.shoots_ui.data.repository.GroupRepository
import com.shoots.shoots_ui.data.repository.ScreenTimeRepository

class HomeViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val groupRepository = GroupRepository(NetworkModule.apiService)
        val screenTimeRepository = ScreenTimeRepository(NetworkModule.apiService)
        return HomeViewModel(groupRepository, screenTimeRepository) as T
    }
}