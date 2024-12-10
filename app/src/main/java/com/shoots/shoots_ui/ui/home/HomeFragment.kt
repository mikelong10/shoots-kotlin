package com.shoots.shoots_ui.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shoots.shoots_ui.data.model.Group

@Composable
fun HomeFragment(
    viewModel: HomeViewModel,
    onNavigateToGroup: (Int) -> Unit
) {
    val homeState by viewModel.homeState.collectAsStateWithLifecycle()
    val isCreateGroupDialogVisible by viewModel.isCreateGroupDialogVisible.collectAsStateWithLifecycle()
    val isJoinGroupDialogVisible by viewModel.isJoinGroupDialogVisible.collectAsStateWithLifecycle()
    val isEnterScreenTimeDialogVisible by viewModel.isEnterScreenTimeDialogVisible.collectAsStateWithLifecycle()

    HomeScreen(
        homeState = homeState,
        onCreateGroupClick = viewModel::showCreateGroupDialog,
        onJoinGroupClick = viewModel::showJoinGroupDialog,
        onEnterScreenTimeClick = viewModel::showEnterScreenTimeDialog,
        onGroupClick = onNavigateToGroup,
        viewModel = viewModel
    )

    if (isCreateGroupDialogVisible) {
        CreateGroupDialog(
            onDismiss = viewModel::hideCreateGroupDialog,
            onCreate = viewModel::createGroup
        )
    }

    if (isJoinGroupDialogVisible) {
        JoinGroupDialog(
            onDismiss = viewModel::hideJoinGroupDialog,
            onJoin = viewModel::joinGroup
        )
    }

    if (isEnterScreenTimeDialogVisible) {
        EnterScreenTimeDialog(
            onDismiss =
            viewModel::hideEnterScreenTimeDialog,
            onEnter = viewModel::enterScreenTime
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    homeState: HomeState,
    onCreateGroupClick: () -> Unit,
    onJoinGroupClick: () -> Unit,
    onEnterScreenTimeClick: () -> Unit,
    onGroupClick: (Int) -> Unit,
    viewModel: HomeViewModel
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Home") },
                actions = {
                    IconButton(onClick = onCreateGroupClick) {
                        Text("+")
                    }
                }
            )
        }
    ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = onCreateGroupClick,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                    ) {
                        Text("Create Group")
                    }
                    Button(
                        onClick = onJoinGroupClick,
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp)
                    ) {
                        Text("Join Group")
                    }
                }

                when (homeState) {
                    is HomeState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    is HomeState.Success -> {
                        Box(modifier = Modifier.fillMaxSize()) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                if (homeState.screenTime != null) {
                                    ScreenTimeCard(screenTime = homeState.screenTime.submitted_time)
                                }

                                if (homeState.groups.isEmpty()) {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            "You are not in any groups, create or join one to get started!",
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                } else {
                                    val scrollState = rememberScrollState()
                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.verticalScroll(scrollState)
                                    ) {
                                        if (homeState.myGroups.isNotEmpty()) {
                                            Header("My Groups")
                                            homeState.myGroups.forEach { group ->
                                                GroupCard(
                                                    group = group,
                                                    onClick = { onGroupClick(group.id) }
                                                )
                                            }
                                        }
                                        Header("Available Groups")
                                        homeState.groups.forEach { group ->
                                            GroupCard(
                                                group = group,
                                                onClick = { onGroupClick(group.id) }
                                            )
                                        }
                                    }
                                }
                            }

                            if (homeState.screenTime == null) {
                                Button(
                                    onClick = onEnterScreenTimeClick,
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .padding(16.dp)
                                ) {
                                    Text("Enter Screen Time")
                                }
                            }
                        }
                    }

                    is HomeState.Error -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                homeState.message,
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(16.dp)
                            )
                            Button(
                                onClick = { viewModel.loadHomeData() },
                                modifier = Modifier.padding(top = 8.dp)
                            ) {
                                Text("Retry")
                            }
                        }
                    }
                }
            }
        }
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Header(text: String) {
    Text(text)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenTimeCard(screenTime: Int?) {
    if (screenTime != null) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiary)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("This Week's Daily Avg", style = MaterialTheme.typography.titleMedium)
                Text("$screenTime minutes", style = MaterialTheme.typography.headlineLarge)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupCard(
    group: Group,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = group.name,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Goal: ${group.screen_time_goal} minutes",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Stake: $${group.stake}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun CreateGroupDialog(
    onDismiss: () -> Unit,
    onCreate: (String, Int, Double) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var screenTimeGoal by remember { mutableStateOf("") }
    var stake by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Group") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Group Name") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = screenTimeGoal,
                    onValueChange = { screenTimeGoal = it },
                    label = { Text("Screen Time Goal (minutes)") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = stake,
                    onValueChange = { stake = it },
                    label = { Text("Stake ($)") }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val goalMinutes = screenTimeGoal.toIntOrNull() ?: 0
                    val stakeAmount = stake.toDoubleOrNull() ?: 0.0
                    if (name.isNotBlank() && goalMinutes > 0 && stakeAmount >= 0) {
                        onCreate(name, goalMinutes, stakeAmount)
                    }
                }
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun JoinGroupDialog(
    onDismiss: () -> Unit,
    onJoin: (String) -> Unit
) {
    var code by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Join Group") },
        text = {
            Column {
                OutlinedTextField(
                    value = code,
                    onValueChange = { code = it },
                    label = { Text("Group Code") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (code.isNotBlank()) {
                                onJoin(code)
                            }
                        }
                    )
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (code.isNotBlank()) {
                        onJoin(code)
                    }
                }
            ) {
                Text("Join")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun EnterScreenTimeDialog(
    onDismiss: () -> Unit,
    onEnter: (Int) -> Unit
) {
    var screenTime by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Screen Time") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = screenTime,
                    onValueChange = { input: String ->
                        if (input.all { it.isDigit() }) {
                            screenTime = input
                        }
                    },
                    label = { Text("Daily Average") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (screenTime.isNotBlank()) {
                                onEnter(screenTime.toInt())
                            }
                        }
                    )
                )
                Text(
                    text = "Enter your daily average screen time for this past week",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (screenTime.isNotBlank()) {
                        onEnter(screenTime.toInt())
                    }
                }
            ) {
                Text("Enter")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}