package com.shoots.shoots_ui.ui.payouts

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.shoots.shoots_ui.data.remote.NetworkModule
import com.shoots.shoots_ui.data.repository.GroupRepository

class PayoutsViewModelFactory(private val context: Context, private val groupId: Int) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val groupRepository = GroupRepository(NetworkModule.apiService)
        return PayoutsViewModel(groupRepository, groupId) as T
    }
}