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
                val payouts = calculatePayouts(group.stake, rankings)

                _payoutsState.value = PayoutsState.Success(
                    group = group,
                    rankings = rankings,
                    payouts = payouts
                )
            } catch (e: Exception) {
                _payoutsState.value = PayoutsState.Error("loadPayoutsData: ${e.message}" ?: "Failed to load payouts data")
            }
        }
    }

    private fun calculatePayouts(stake: Double, rankings: List<Ranking>): Map<Int, Double> {
        val payouts = rankings.associate { ranking ->
            val payout = when (ranking.rank) {
                1 -> stake * 0.5
                2 -> stake * 0.3
                3 -> stake * 0.2
                else -> 0.0
            }
            ranking.user.id to payout
        }
        return payouts
    }
}