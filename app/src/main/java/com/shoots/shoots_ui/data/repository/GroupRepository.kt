package com.shoots.shoots_ui.data.repository

import com.shoots.shoots_ui.data.model.CreateGroupRequest
import com.shoots.shoots_ui.data.model.Group
import com.shoots.shoots_ui.data.model.JoinGroupRequest
import com.shoots.shoots_ui.data.model.Ranking
import com.shoots.shoots_ui.data.model.ScreenTime
import com.shoots.shoots_ui.data.model.UserHistoricalRankings
import com.shoots.shoots_ui.data.remote.ApiService

class GroupRepository(
    private val apiService: ApiService
) {
    suspend fun listGroups(): List<Group> {
        val response = apiService.listGroups()
        if (response.success) {
            return response.data
        } else {
            throw Exception(response.message)
        }
    }

    suspend fun listMyGroups(): List<Group> {
        val response = apiService.listMyGroups()
        if (response.success) {
            return response.data
        } else {
            throw Exception(response.message)
        }
    }

    suspend fun getGroup(id: Int): Group {
        val response = apiService.getGroup(id)
        if (response.success) {
            return response.data
        } else {
            throw Exception(response.message)
        }
    }

    suspend fun createGroup(name: String, screenTimeGoal: Int, stake: Double): Group {
        val response = apiService.createGroup(CreateGroupRequest(name, screenTimeGoal, stake))
        if (response.success) {
            return response.data
        } else {
            throw Exception(response.message)
        }
    }

    suspend fun joinGroup(code: String): Group {
        val response = apiService.joinGroup(JoinGroupRequest(code))
        if (response.success) {
            return response.data
        } else {
            throw Exception(response.message)
        }
    }

    suspend fun createInvite(groupId: Int): String {
        val response = apiService.createInvite(groupId)
        if (response.success) {
            return response.data
        } else {
            throw Exception(response.message)
        }
    }

    suspend fun getGroupScreenTime(id: Int): List<ScreenTime> {
        val response = apiService.getGroupScreenTime(id)
        if (response.success) {
            return response.data
        } else {
            throw Exception(response.message)
        }
    }

    suspend fun getWeeklyRankings(id: Int): List<Ranking> {
        val response = apiService.getWeeklyRankings(id)
        if (response.success) {
            return response.data
        } else {
            throw Exception(response.message)
        }
    }

    suspend fun getHistoricalRankings(id: Int): List<UserHistoricalRankings> {
        val response = apiService.getHistoricalRankings(id)
        if (response.success) {
            return response.data
        } else {
            throw Exception(response.message)
        }
    }

    suspend fun addScreenTime(groupId: Int, time: Double): List<ScreenTime> {
        val response = apiService.addScreenTime(groupId, time)
        if (response.success) {
            return response.data
        } else {
            throw Exception(response.message)
        }
    }
}