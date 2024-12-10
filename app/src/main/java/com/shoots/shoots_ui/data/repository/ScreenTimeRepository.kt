package com.shoots.shoots_ui.data.repository;

import com.shoots.shoots_ui.data.model.CreateScreenTimeRequest
import com.shoots.shoots_ui.data.model.ScreenTime
import com.shoots.shoots_ui.data.remote.ApiService;

class ScreenTimeRepository(
    private val apiService: ApiService
) {
    suspend fun enterScreenTime(screenTime: Int): ScreenTime {
        val response = apiService.enterScreenTime(CreateScreenTimeRequest(screenTime))
        if (response.success) {
            return response.data
        } else {
            throw Exception(response.message)
        }
    }
}