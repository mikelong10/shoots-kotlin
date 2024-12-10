package com.shoots.shoots_ui.ui

sealed class NavigationItem(val route: String) {
    object Auth : NavigationItem("auth")
    object User : NavigationItem("user")
}