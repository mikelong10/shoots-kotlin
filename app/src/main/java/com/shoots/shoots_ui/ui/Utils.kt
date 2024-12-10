package com.shoots.shoots_ui.ui

fun formatDisplayScreenTime(minutes: Int): String {
    val hours = minutes / 60
    val remainingMinutes = minutes % 60
    if (hours == 0) {
        return "${remainingMinutes}min"
    }
    if (remainingMinutes == 0) {
        return "${hours}hr"
    }
    return "${hours}hr ${remainingMinutes}min"
}