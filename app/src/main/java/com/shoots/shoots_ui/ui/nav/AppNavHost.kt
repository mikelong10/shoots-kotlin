package com.shoots.shoots_ui.ui.nav

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.shoots.shoots_ui.ui.NavigationItem
import com.shoots.shoots_ui.ui.auth.AuthFragment
import com.shoots.shoots_ui.ui.user.UserFragment

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: String = NavigationItem.Auth.route
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable(NavigationItem.Auth.route) {
            AuthFragment()
        }
        composable(NavigationItem.User.route) {
            UserFragment()
        }
    }
}