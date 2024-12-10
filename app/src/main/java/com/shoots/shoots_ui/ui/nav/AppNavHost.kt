package com.shoots.shoots_ui.ui.nav

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.shoots.shoots_ui.ui.NavigationItem
import com.shoots.shoots_ui.ui.auth.AuthFragment
import com.shoots.shoots_ui.ui.auth.AuthState
import com.shoots.shoots_ui.ui.auth.AuthViewModel
import com.shoots.shoots_ui.ui.auth.AuthViewModelFactory
import com.shoots.shoots_ui.ui.user.UserFragment

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: String = NavigationItem.Auth.route
) {
    val context = LocalContext.current
    val authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(context))
    val authState by authViewModel.authState.collectAsState(initial = AuthState.Initial)

    // Handle authentication state changes
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.NotAuthenticated -> {
                navController.navigate(NavigationItem.Auth.route) {
                    // Pop up to the start destination to remove everything from the back stack
                    popUpTo(0) { inclusive = true }
                }
            }
            is AuthState.Authenticated -> {
                navController.navigate(NavigationItem.User.route) {
                    // Remove Auth screen from back stack when authenticated
                    popUpTo(NavigationItem.Auth.route) { inclusive = true }
                }
            }
            else -> {} // Handle other states if needed
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(NavigationItem.Auth.route) {
            AuthFragment(
                viewModel = authViewModel,
                onNavigateToUser = {
                    navController.navigate(NavigationItem.User.route) {
                        popUpTo(NavigationItem.Auth.route) { inclusive = true }
                    }
                }
            )
        }
        composable(NavigationItem.User.route) {
            UserFragment(viewModel = authViewModel)
        }
    }
}