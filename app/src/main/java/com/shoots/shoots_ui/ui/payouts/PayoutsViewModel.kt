package com.shoots.shoots_ui.ui.payouts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shoots.shoots_ui.data.model.Group
import com.shoots.shoots_ui.data.model.Ranking
import com.shoots.shoots_ui.data.repository.GroupRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class PayoutsState {
    data object Loading : PayoutsState()
    data class Success(
        val group: Group,
        val rankings: List<Ranking>,
        val payouts: Map<Int, Double>
    ) : PayoutsState()

    data class Error(val message: String) : PayoutsState()
}

class PayoutsViewModel(
    private val groupRepository: GroupRepository,
    private val groupId: Int
) : ViewModel() {

    private val _payoutsState = MutableStateFlow<PayoutsState>(PayoutsState.Loading)
    val payoutsState: StateFlow<PayoutsState> = _payoutsState

    init {
        loadPayoutsData()
    }

    private fun loadPayoutsData() {
        viewModelScope.launch {
            try {
                val group = groupRepository.getGroup(groupId)
                val rankings = groupRepository.getWeeklyRankings(groupId)
                val payouts = calculatePayouts(group.stake, group.screen_time_goal, rankings)

                _payoutsState.value = PayoutsState.Success(
                    group = group,
                    rankings = rankings,
                    payouts = payouts
                )
            } catch (e: Exception) {
                _payoutsState.value = PayoutsState.Error(
                    "loadPayoutsData: ${e.message}" ?: "Failed to load payouts data"
                )
            }
        }
    }

    private fun calculatePayouts(
        stake: Double,
        screenTimeGoal: Int,
        rankings: List<Ranking>
    ): Map<Int, Double> {
        val belowGoal = rankings.filter { it.time < screenTimeGoal }
        val aboveGoal = rankings.filter { it.time >= screenTimeGoal }

        val payouts = mutableMapOf<Int, Double>()

        // Users below the goal receive payments
        for (user in belowGoal) {
            payouts[user.user.id] = aboveGoal.size * stake
        }

        // Users above the goal pay the stake amount for each user below the goal
        for (user in aboveGoal) {
            payouts[user.user.id] = -(belowGoal.size * stake)
        }

        return payouts
    }
}