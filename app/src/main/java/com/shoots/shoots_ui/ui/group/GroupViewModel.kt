package com.shoots.shoots_ui.ui.group

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shoots.shoots_ui.data.model.Group
import com.shoots.shoots_ui.data.model.Ranking
import com.shoots.shoots_ui.data.model.ScreenTime
import com.shoots.shoots_ui.data.model.User
import com.shoots.shoots_ui.data.model.UserHistoricalRankings
import com.shoots.shoots_ui.data.repository.GroupRepository
import com.shoots.shoots_ui.data.repository.ScreenTimeRepository
import com.shoots.shoots_ui.ui.home.HomeState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class GroupState {
    data object Loading : GroupState()
    data class Success(
        val group: Group,
        val weeklyRankings: List<Ranking>,
        val members: List<User>,
        val historicalRankings: List<UserHistoricalRankings>,
        val screenTimes: List<ScreenTime>
    ) : GroupState()
    data class Error(val message: String) : GroupState()
}

class GroupViewModel(
    private val repository: GroupRepository,
    private val groupId: Int,
    private val screenTimeRepository: ScreenTimeRepository
) : ViewModel() {
    private val _groupState = MutableStateFlow<GroupState>(GroupState.Loading)
    val groupState: StateFlow<GroupState> = _groupState

    private val _isHistoricalView = MutableStateFlow(false)
    val isHistoricalView: StateFlow<Boolean> = _isHistoricalView

    init {
        loadGroupData()
    }

    fun loadGroupData() {
        viewModelScope.launch {
            _groupState.value = GroupState.Loading
            try {
                val group = repository.getGroup(groupId)
                val weeklyRankings = repository.getWeeklyRankings(groupId)
                val historicalRankings = repository.getHistoricalRankings(groupId)
                val screenTimes = repository.getGroupScreenTime(groupId)
                val members = repository.getGroupMembers(groupId)

                _groupState.value = GroupState.Success(
                    group = group,
                    weeklyRankings = weeklyRankings,
                    members = members,
                    historicalRankings = historicalRankings,
                    screenTimes = screenTimes
                )
            } catch (e: Exception) {
                _groupState.value = GroupState.Error(e.message ?: "Failed to load group data")
            }
        }
    }

    fun joinGroup(code: String) {
        viewModelScope.launch {
            try {
                repository.joinGroup(code)
                loadGroupData()
            } catch (e: Exception) {
                _groupState.value = GroupState.Error(e.message ?: "Failed to join group")
            }
        }
    }

    fun toggleHistoricalView() {
        _isHistoricalView.value = !_isHistoricalView.value
    }
}