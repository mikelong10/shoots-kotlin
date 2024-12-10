package com.shoots.shoots_ui.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shoots.shoots_ui.data.model.Group
import com.shoots.shoots_ui.data.repository.GroupRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class HomeState {
    object Loading : HomeState()
    data class Success(val groups: List<Group>) : HomeState()
    data class Error(val message: String) : HomeState()
}

class HomeViewModel(
    private val repository: GroupRepository
) : ViewModel() {
    private val _homeState = MutableStateFlow<HomeState>(HomeState.Loading)
    val homeState: StateFlow<HomeState> = _homeState

    private val _isCreateGroupDialogVisible = MutableStateFlow(false)
    val isCreateGroupDialogVisible: StateFlow<Boolean> = _isCreateGroupDialogVisible

    private val _isJoinGroupDialogVisible = MutableStateFlow(false)
    val isJoinGroupDialogVisible: StateFlow<Boolean> = _isJoinGroupDialogVisible

    init {
        loadGroups()
    }

    fun loadGroups() {
        viewModelScope.launch {
            _homeState.value = HomeState.Loading
            try {
                val groups = repository.listGroups()
                _homeState.value = HomeState.Success(groups)
            } catch (e: Exception) {
                _homeState.value = HomeState.Error(e.message ?: "Failed to load groups")
            }
        }
    }

    fun showCreateGroupDialog() {
        _isCreateGroupDialogVisible.value = true
    }

    fun hideCreateGroupDialog() {
        _isCreateGroupDialogVisible.value = false
    }

    fun showJoinGroupDialog() {
        _isJoinGroupDialogVisible.value = true
    }

    fun hideJoinGroupDialog() {
        _isJoinGroupDialogVisible.value = false
    }

    fun createGroup(name: String, screenTimeGoal: Int, stake: Double) {
        viewModelScope.launch {
            try {
                repository.createGroup(name, screenTimeGoal, stake)
                hideCreateGroupDialog()
                loadGroups()
            } catch (e: Exception) {
                _homeState.value = HomeState.Error(e.message ?: "Failed to create group")
            }
        }
    }

    fun joinGroup(code: String) {
        viewModelScope.launch {
            try {
                repository.joinGroup(code)
                hideJoinGroupDialog()
                loadGroups()
            } catch (e: Exception) {
                _homeState.value = HomeState.Error(e.message ?: "Failed to join group")
            }
        }
    }
}