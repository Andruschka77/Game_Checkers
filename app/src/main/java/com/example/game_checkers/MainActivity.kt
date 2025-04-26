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
import com.example.game_checkers.ui.screen.CheckerStyle
import com.example.game_checkers.ui.screen.CheckersScreen
import com.example.game_checkers.ui.screen.SettingsScreen
import com.example.game_checkers.ui.screen.WelcomeScreen
import com.example.game_checkers.ui.screen.WinnerScreen
import com.example.game_checkers.utils.Player
import com.example.game_checkers.viewmodel.CheckersViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var isDarkTheme by remember { mutableStateOf(false) }
            var currentScreen by remember { mutableStateOf<Screen>(Screen.Welcome) }
            var currentCheckerStyle by remember { mutableStateOf(CheckerStyle.CLASSIC) }
            val viewModel: CheckersViewModel = viewModel()

            Game_CheckersTheme(
                darkTheme = isDarkTheme,
                checkerStyle = currentCheckerStyle
            ) {
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
                                viewModel.saveCurrentGame()
                                currentScreen = Screen.Welcome
                            },
                            onGameFinished = { winner ->
                                viewModel.clearSavedGame()
                                currentScreen = Screen.Winner(winner)
                            },
                            checkerStyle = currentCheckerStyle
                        )

                        is Screen.Settings -> SettingsScreen(
                            isDarkTheme = isDarkTheme,
                            currentCheckerStyle = currentCheckerStyle,
                            onThemeChanged = { isDarkTheme = it },
                            onCheckerStyleChanged = { style ->
                                currentCheckerStyle = style
                            },
                            onBackPressed = { currentScreen = Screen.Welcome }
                        )

                        is Screen.Winner -> WinnerScreen(
                            winner = (currentScreen as Screen.Winner).winner,
                            checkerStyle = currentCheckerStyle,
                            onExit = {
                                viewModel.resetGame()
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