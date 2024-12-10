package com.shoots.shoots_ui.ui.payouts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun PayoutsPage(groupId: Int, navController: NavController) {
    // Implement the UI for the Payouts page here
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Payouts for Group $groupId", style = MaterialTheme.typography.headlineLarge)
        // Add more UI elements to display the final leaderboard and payouts
        Button(onClick = { navController.navigateUp() }) {
            Text("Back")
        }
    }
}