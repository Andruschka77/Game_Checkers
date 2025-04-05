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

    fun makeMove(position: Position) {
        viewModelScope.launch {
            val currentState = _gameState.value
            if (currentState.selectedPiece == null) {
                // Выбираем шашку
                _gameState.value = currentState.copy(selectedPiece = position)
            } else {
                // Ищем допустимый ход
                val move = currentState.possibleMoves.firstOrNull { it.to == position }
                if (move != null) {
                    // Выполняем ход
                    currentState.makeMove(move)
                    _gameState.value = currentState.copy() // Обновляем состояние
                } else {
                    // Если ход недопустим, выбираем другую шашку
                    _gameState.value = currentState.copy(selectedPiece = position)
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