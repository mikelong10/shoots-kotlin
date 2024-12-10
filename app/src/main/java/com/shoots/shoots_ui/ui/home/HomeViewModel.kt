package com.shoots.shoots_ui.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shoots.shoots_ui.data.model.Group
import com.shoots.shoots_ui.data.model.ScreenTime
import com.shoots.shoots_ui.data.repository.GroupRepository
import com.shoots.shoots_ui.data.repository.ScreenTimeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class HomeState {
    object Loading : HomeState()
    data class Success(
        val groups: List<Group>, 
        val myGroups: List<Group>,
        val screenTime: ScreenTime?
    ) : HomeState()
    data class Error(val message: String) : HomeState()
}

class HomeViewModel(
    private val groupRepository: GroupRepository,
    private val screenTimeRepository: ScreenTimeRepository
) : ViewModel() {
    private val _homeState = MutableStateFlow<HomeState>(HomeState.Loading)
    val homeState: StateFlow<HomeState> = _homeState

    private val _isCreateGroupDialogVisible = MutableStateFlow(false)
    val isCreateGroupDialogVisible: StateFlow<Boolean> = _isCreateGroupDialogVisible

    private val _isJoinGroupDialogVisible = MutableStateFlow(false)
    val isJoinGroupDialogVisible: StateFlow<Boolean> = _isJoinGroupDialogVisible

    private val _isEnterScreenTimeDialogVisible = MutableStateFlow(false)
    val isEnterScreenTimeDialogVisible: StateFlow<Boolean> = _isEnterScreenTimeDialogVisible

    init {
        loadHomeData()
    }

    fun loadHomeData() {
        viewModelScope.launch {
            _homeState.value = HomeState.Loading
            try {
                val myGroups = groupRepository.listMyGroups()
                val myGroupIds = myGroups.map { it.id }.toSet()

                val allGroups = groupRepository.listGroups()
                val availableGroups = allGroups.filterNot { it.id in myGroupIds }

                val screenTime = screenTimeRepository.getSelfScreenTime()

                _homeState.value = HomeState.Success(
                    groups = availableGroups,
                    myGroups = myGroups,
                    screenTime = screenTime
                )
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

    fun showEnterScreenTimeDialog() {
        _isEnterScreenTimeDialogVisible.value = true
    }

    fun hideEnterScreenTimeDialog() {
        _isEnterScreenTimeDialogVisible.value = false
    }

    fun createGroup(name: String, screenTimeGoal: Int, stake: Double) {
        viewModelScope.launch {
            try {
                val newGroup = groupRepository.createGroup(name, screenTimeGoal, stake)
                hideCreateGroupDialog()
                
                val currentState = _homeState.value
                if (currentState is HomeState.Success) {
                    _homeState.value = currentState.copy(
                        myGroups = currentState.myGroups + newGroup
                    )
                }
            } catch (e: Exception) {
                _homeState.value = HomeState.Error(e.message ?: "Failed to create group")
            }
        }
    }

    fun joinGroup(code: String) {
        viewModelScope.launch {
            try {
                groupRepository.joinGroup(code)
                hideJoinGroupDialog()
                loadHomeData()
            } catch (e: Exception) {
                _homeState.value = HomeState.Error(e.message ?: "Failed to join group")
            }
        }
    }

    fun enterScreenTime(screenTime: Int) {
        viewModelScope.launch {
            try {
                screenTimeRepository.enterScreenTime(screenTime)
                hideEnterScreenTimeDialog()
                loadHomeData()
            } catch (e: Exception) {
                _homeState.value = HomeState.Error(e.message ?: "Failed to enter screen time")
            }
        }
    }
}