package com.example.game_checkers

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.game_checkers.ui.theme.Game_CheckersTheme
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var isDarkTheme by remember { mutableStateOf(false) }
            var currentScreen by remember { mutableStateOf<Screen>(Screen.Welcome) }
            val viewModel: CheckersViewModel = viewModel()

            Game_CheckersTheme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    when (currentScreen) {
                        is Screen.Welcome -> WelcomeScreen(
                            viewModel = viewModel,
                            onStartGame = {
                                viewModel.resetGame()
                                currentScreen = Screen.Game
                            },
                            onContinueGame = {
                                viewModel.loadSavedGame()
                                currentScreen = Screen.Game
                            },
                            onSettingsClicked = { currentScreen = Screen.Settings }
                        )

                        is Screen.Game -> CheckersScreen(
                            viewModel = viewModel,
                            onBackPressed = {
                                viewModel.saveCurrentGame() // Сохраняем при выходе
                                currentScreen = Screen.Welcome
                            },
                            onGameFinished = { winner ->
                                viewModel.resetGame() // Сбрасываем сохранённую игру при завершении
                                currentScreen = Screen.Winner(winner)
                            }
                        )

                        is Screen.Settings -> SettingsScreen(
                            isDarkTheme = isDarkTheme,
                            onThemeChanged = { isDarkTheme = it },
                            onBackPressed = { currentScreen = Screen.Welcome }
                        )

                        is Screen.Winner -> WinnerScreen(
                            winner = (currentScreen as Screen.Winner).winner,
                            onExit = {
                                viewModel.clearSavedGame() // Очищаем сохранённую игру
                                currentScreen = Screen.Welcome
                            }
                        )
                    }
                }
            }
        }
    }
}

sealed class Screen {
    object Welcome : Screen()
    object Game : Screen()
    object Settings : Screen()
    data class Winner(val winner: Player) : Screen()
}