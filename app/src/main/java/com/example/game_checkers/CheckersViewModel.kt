package com.example.game_checkers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CheckersViewModel : ViewModel() {
    private val _gameState = MutableStateFlow(CheckersGameState())
    val gameState: StateFlow<CheckersGameState> = _gameState

    private var savedState: CheckersGameState? = null

    fun clearSavedGame() {
        savedState = null
    }

    fun saveCurrentGame() {
        savedState = _gameState.value.copy()
    }

    fun loadSavedGame() {
        savedState?.let {
            _gameState.value = it
        }
    }

    fun hasSavedGame(): Boolean = savedState != null

    fun makeMove(move: Move) {
        viewModelScope.launch {
            val currentState = _gameState.value

            if (currentState.selectedPiece == null) {
                // Выбираем шашку (используем from из Move)
                _gameState.value = currentState.copy(selectedPiece = move.from)
            } else {
                // Проверяем, допустим ли ход
                if (move in currentState.possibleMoves) {
                    // Выполняем ход через CheckersGameState
                    val success = currentState.makeMove(move)

                    if (success) {
                        // Если ход успешен, обновляем состояние
                        _gameState.value = currentState // Обновляем текущее состояние
                    }
                } else {
                    // Если ход недопустим, выбираем другую шашку
                    _gameState.value = currentState.copy(selectedPiece = move.from)
                }
            }
        }
    }

    fun selectPiece(position: Position) {
        val currentState = _gameState.value
        currentState.selectPiece(position)
        _gameState.value = currentState.copy() // Обновляем состояние
    }

    fun resetGame() {
        _gameState.value = CheckersGameState()
    }
}