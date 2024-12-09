package com.shoots.shoots_ui.ui.nav

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.shoots.shoots_ui.ui.NavigationItem
import com.shoots.shoots_ui.ui.auth.AuthContent
import com.shoots.shoots_ui.ui.auth.AuthViewModel
import com.shoots.shoots_ui.ui.auth.AuthViewModelFactory
import com.shoots.shoots_ui.ui.user.UserContent

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: String = NavigationItem.Auth.route
) {
    val context = LocalContext.current
    val authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(context))

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(NavigationItem.Auth.route) {
            AuthContent(
                viewModel = authViewModel,
                onNavigateToUser = {
                    navController.navigate(NavigationItem.User.route) {
                        popUpTo(NavigationItem.Auth.route) { inclusive = true }
                    }
                }
            )
        }
        composable(NavigationItem.User.route) {
            UserContent(viewModel = authViewModel)
        }
    }
}