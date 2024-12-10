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
                // Get all groups first
                val allGroups = groupRepository.listGroups()
                
                // Get my groups and create a set of their IDs
                val myGroups = groupRepository.listMyGroups()
                val myGroupIds = myGroups.map { it.id }.toSet()
                
                // Filter available groups to exclude my groups
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
                groupRepository.createGroup(name, screenTimeGoal, stake)
                hideCreateGroupDialog()
                loadHomeData()
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
                if (screenTime < 0) {
                    _homeState.value = HomeState.Error("Screen time must be a positive number")
                    return@launch
                }
                
                screenTimeRepository.enterScreenTime(screenTime)
                hideEnterScreenTimeDialog()
                loadHomeData()
            } catch (e: Exception) {
                val errorMessage = when {
                    e.message?.contains("already submitted") == true -> 
                        "You have already submitted time for this week"
                    e.message?.contains("must be an integer") == true ->
                        "Screen time must be an integer"
                    else -> e.message ?: "Failed to enter screen time"
                }
                _homeState.value = HomeState.Error(errorMessage)
            }
        }
    }
}