package com.shoots.shoots_ui.ui

sealed class NavigationItem(val route: String) {
    object Auth : NavigationItem("auth")
    object Home : NavigationItem("home")
    object User : NavigationItem("user")
    object Group : NavigationItem("group/{groupId}") {
        fun createRoute(groupId: Int) = "group/$groupId"
    }
}