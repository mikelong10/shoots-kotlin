package com.shoots.shoots_ui.ui.group

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.shoots.shoots_ui.data.remote.NetworkModule
import com.shoots.shoots_ui.data.repository.GroupRepository
import com.shoots.shoots_ui.data.repository.ScreenTimeRepository

class GroupViewModelFactory(
    private val context: Context,
    private val groupId: Int
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val repository = GroupRepository(NetworkModule.apiService)
        val screenRepository = ScreenTimeRepository(NetworkModule.apiService)
        return GroupViewModel(repository, groupId, screenRepository) as T
    }
} 