package com.shoots.shoots_ui.ui.screen_time

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class EnterScreenTimeViewModel: ViewModel() {
    private val _userPastWeekAvgScreenTime = MutableLiveData<Number>()

    fun enterPastWeekAvgScreenTime(screenTime: Number) {
        _userPastWeekAvgScreenTime.value = screenTime
    }
}