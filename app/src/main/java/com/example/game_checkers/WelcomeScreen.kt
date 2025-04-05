package com.example.game_checkers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun WelcomeScreen(
    viewModel: CheckersViewModel,
    onStartGame: () -> Unit,
    onContinueGame: () -> Unit,
    onSettingsClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Добро пожаловать в шашки!",
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (viewModel.hasSavedGame()) {
            Button(
                onClick = onContinueGame,
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Text("Продолжить текущую игру")
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        Button(
            onClick = onStartGame,
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text("Новая игра")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onSettingsClicked,
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text("Настройки")
        }
    }
}