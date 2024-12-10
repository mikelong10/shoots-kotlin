package com.shoots.shoots_ui.ui.payouts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.shoots.shoots_ui.data.model.Ranking

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PayoutsPage(groupId: Int, navController: NavController) {
    val viewModel: PayoutsViewModel = viewModel(factory = PayoutsViewModelFactory(LocalContext.current, groupId))
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
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Payouts for ${state.group.name}",
                            style = MaterialTheme.typography.headlineMedium
                        )
                        PayoutsList(state.rankings, state.payouts, navController)
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
fun PayoutsList(rankings: List<Ranking>, payouts: Map<Int, Double>, navController: NavController) {
    for (ranking in rankings) {
        val payout = payouts[ranking.user.id] ?: 0.0
        PayoutItem(ranking, payout, navController)
    }
}

@Composable
fun PayoutItem(rank: Ranking, payout: Double, navController: NavController) {
    Column {
        Text("Rank: ${rank.rank}")
        Text("Payout: $payout")
    }
}