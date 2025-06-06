package com.example.game_checkers.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.game_checkers.utils.CheckersGameState
import com.example.game_checkers.utils.Move
import com.example.game_checkers.utils.Position
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
                _gameState.value = currentState.copy(selectedPiece = move.from)
            } else {
                if (move in currentState.possibleMoves) {
                    val success = currentState.makeMove(move)

                    if (success) {
                        _gameState.value = currentState
                    }
                } else {
                    _gameState.value = currentState.copy(selectedPiece = move.from)
                }
            }
        }
    }

    fun selectPiece(position: Position) {
        val currentState = _gameState.value
        currentState.selectPiece(position)
        _gameState.value = currentState.copy()
    }

    fun resetGame() {
        _gameState.value = CheckersGameState()
    }
}