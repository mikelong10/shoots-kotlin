package com.shoots.shoots_ui.ui.nav

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.shoots.shoots_ui.ui.NavigationItem
import com.shoots.shoots_ui.ui.auth.AuthFragment
import com.shoots.shoots_ui.ui.auth.AuthState
import com.shoots.shoots_ui.ui.auth.AuthViewModel
import com.shoots.shoots_ui.ui.auth.AuthViewModelFactory
import com.shoots.shoots_ui.ui.home.HomeFragment
import com.shoots.shoots_ui.ui.home.HomeViewModel
import com.shoots.shoots_ui.ui.home.HomeViewModelFactory
import com.shoots.shoots_ui.ui.user.UserFragment
import com.shoots.shoots_ui.ui.group.GroupFragment
import com.shoots.shoots_ui.ui.payouts.PayoutsPage

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: String = NavigationItem.Auth.route
) {
    val context = LocalContext.current
    val authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(context))
    val authState by authViewModel.authState.collectAsState()

    println("Current auth state: $authState") // Debug log

    LaunchedEffect(authState) {
        println("LaunchedEffect triggered with state: $authState") // Debug log
        when (authState) {
            is AuthState.Authenticated -> {
                println("Navigating to Home") // Debug log
                navController.navigate(NavigationItem.Home.route) {
                    popUpTo(NavigationItem.Auth.route) { inclusive = true }
                }
            }
            is AuthState.NotAuthenticated -> {
                println("Navigating to Auth") // Debug log
                if (navController.currentDestination?.route != NavigationItem.Auth.route) {
                    navController.navigate(NavigationItem.Auth.route) {
                        popUpTo(0) { inclusive = true }
                    }
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
            AuthFragment(viewModel = authViewModel)
        }

        composable(NavigationItem.Home.route) {
            val homeViewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(context))
            HomeFragment(
                viewModel = homeViewModel,
                onNavigateToGroup = { groupId ->
                    navController.navigate(NavigationItem.Group.createRoute(groupId))
                },
                onNavigateToProfile = {
                    navController.navigate("user") },
                authModel = authViewModel
            )
        }

        composable(NavigationItem.User.route) {
            UserFragment(viewModel = authViewModel, onGoBack = {navController.navigateUp()})
        }

        composable(
            route = NavigationItem.Group.route,
            arguments = listOf(navArgument("groupId") { type = NavType.IntType })
        ) {
            val groupId = it.arguments?.getInt("groupId") ?: return@composable
            GroupFragment(
                groupId = groupId,
                navController = navController,
                authModel = authViewModel,
                onNavigateToPayouts = { groupId ->
                    navController.navigate(NavigationItem.Payout.createRoute(groupId))
                }
            )
        }

        composable(
            route = NavigationItem.Payout.route,
            arguments = listOf(navArgument("groupId") { type = NavType.IntType })
        ) {
            val groupId = it.arguments?.getInt("groupId") ?: return@composable
            PayoutsPage(groupId = groupId, navController = navController)
        }
    }
}