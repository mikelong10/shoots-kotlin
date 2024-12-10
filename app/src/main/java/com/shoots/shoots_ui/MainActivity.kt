package com.shoots.shoots_ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.shoots.shoots_ui.data.local.DatabaseModule
import com.shoots.shoots_ui.data.remote.NetworkModule
import com.shoots.shoots_ui.ui.auth.AuthViewModel
import com.shoots.shoots_ui.ui.auth.AuthViewModelFactory
import com.shoots.shoots_ui.ui.nav.AppNavHost
import com.shoots.shoots_ui.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    private val viewModel: AuthViewModel by viewModels {
        AuthViewModelFactory(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize modules with proper dependencies
        val database = DatabaseModule.getDatabase(applicationContext)
        NetworkModule.initialize(
            context = this,
            dao = database.userDao(),
            viewModel = viewModel
        )

        setContent {
            MainScreenContent()
        }
    }
}

@Composable
fun MainScreenContent() {
    AppTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            AppNavHost(
                navController = rememberNavController()
            )
        }
    }
}