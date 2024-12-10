package com.shoots.shoots_ui.ui

import java.text.NumberFormat
import java.util.Locale

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

fun formatUSD(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale.US)
    return format.format(amount)
}