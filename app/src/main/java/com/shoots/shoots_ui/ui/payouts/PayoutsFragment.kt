package com.shoots.shoots_ui.ui.payouts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.shoots.shoots_ui.R
import com.shoots.shoots_ui.data.model.Ranking
import com.shoots.shoots_ui.ui.formatUSD

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PayoutsPage(groupId: Int, navController: NavController) {
    val viewModel: PayoutsViewModel =
        viewModel(factory = PayoutsViewModelFactory(LocalContext.current, groupId))
    val payoutsState by viewModel.payoutsState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    when (val state = payoutsState) {
                        is PayoutsState.Success -> Text(state.group.name)
                        else -> Text("Payouts")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (val state = payoutsState) {
                is PayoutsState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is PayoutsState.Success -> {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            "Payouts for ${state.group.name}",
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Text("Stake: ${formatUSD(state.group.stake)}")
                        PayoutsList(
                            state.rankings,
                            state.payouts,
                            state.group.screen_time_goal,
                            navController
                        )
                    }
                }

                is PayoutsState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = state.message,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PayoutsList(
    rankings: List<Ranking>,
    payouts: Map<Int, Double>,
    screenTimeGoal: Int,
    navController: NavController
) {
    val winners = rankings.filter { it.time < screenTimeGoal }
    val losers = rankings.filter { it.time >= screenTimeGoal }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text(
                "Winners",
                style = MaterialTheme.typography.headlineSmall,
                color = colorResource(R.color.dark_green)
            )
        }
        items(winners) { ranking ->
            val payout = payouts[ranking.user.id] ?: 0.0
            PayoutItem(ranking, payout, navController, isWinner = true)
        }

        item {
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                thickness = 1.dp,
                color = Color.Gray
            )
        }

        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Losers",
                    style = MaterialTheme.typography.headlineSmall,
                    color = colorResource(R.color.dark_red)
                )
                Text(
                    "pay the stake to each winner!",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
        items(losers) { ranking ->
            val payout = payouts[ranking.user.id] ?: 0.0
            PayoutItem(ranking, payout, navController, isWinner = false)
        }
    }
}

@Composable
fun PayoutItem(rank: Ranking, payout: Double, navController: NavController, isWinner: Boolean) {
    val cardColor =
        if (isWinner) colorResource(R.color.light_green) else colorResource(R.color.light_red)

    Card(
        modifier = Modifier.padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("#${rank.rank}", style = MaterialTheme.typography.bodyLarge)
                Text(
                    rank.user.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            Text("Payout: $payout", style = MaterialTheme.typography.bodyLarge)
        }
    }
}