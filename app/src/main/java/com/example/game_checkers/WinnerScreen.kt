package com.example.game_checkers

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Modifier

@Composable
fun WinnerScreen(
    winner: Player,
    checkerStyle: CheckerStyle,
    onExit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val winnerName = if (winner == Player.WHITE) checkerStyle.lightName else checkerStyle.darkName

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Победили $winnerName!",
                style = MaterialTheme.typography.headlineLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onExit) {
                Text("В главное меню")
            }
        }
    }
}