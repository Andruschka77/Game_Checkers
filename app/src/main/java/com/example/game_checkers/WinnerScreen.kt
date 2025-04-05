package com.example.game_checkers

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay

@Composable
fun WinnerScreen(
    winner: Player,
    onExit: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showScreen by remember { mutableStateOf(true) }

    LaunchedEffect(showScreen) {
        delay(2000)
        showScreen = false
        onExit()
    }

    if (showScreen) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Победили ${if (winner == Player.WHITE) "Белые!" else "Чёрные!"}",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(16.dp))
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}