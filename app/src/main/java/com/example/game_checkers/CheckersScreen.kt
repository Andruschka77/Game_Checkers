package com.example.game_checkers

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun CheckersScreen(
    viewModel: CheckersViewModel,
    onBackPressed: () -> Unit,
    onGameFinished: (Player) -> Unit,
    checkerStyle: CheckerStyle,
    modifier: Modifier = Modifier
) {
    val gameState by viewModel.gameState.collectAsState()
    val configuration = LocalConfiguration.current
    val cellSize = remember(configuration) {
        minOf(
            configuration.screenWidthDp.dp,
            configuration.screenHeightDp.dp * 0.8f
        ) / 8
    }

    if (gameState.winner != null) {
        LaunchedEffect(gameState.winner) {
            onGameFinished(gameState.winner!!)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CurrentPlayerIndicator(
                currentPlayer = gameState.currentPlayer,
                winner = gameState.winner,
                checkerStyle = checkerStyle
            )

            Spacer(modifier = Modifier.height(16.dp))

            BoardGrid(
                gameState = gameState,
                checkerStyle = checkerStyle,
                cellSize = cellSize,
                onPieceSelected = { position ->
                    // Просто передаем позицию в ViewModel, вся логика там
                    viewModel.selectPiece(position)

                    // Если шашка уже выбрана и это возможный ход - делаем ход
                    gameState.selectedPiece?.let { selected ->
                        gameState.possibleMoves.firstOrNull { it.to == position }?.let { move ->
                            viewModel.makeMove(move)
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            CapturedPiecesCounter(
                whiteCaptured = gameState.getCapturedPiecesCount(Player.BLACK),
                blackCaptured = gameState.getCapturedPiecesCount(Player.WHITE),
                checkerStyle = checkerStyle
            )
        }

        Button(
            onClick = onBackPressed,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        ) {
            Text("Назад")
        }
    }
}

@Composable
private fun CurrentPlayerIndicator(
    currentPlayer: Player,
    winner: Player?,
    checkerStyle: CheckerStyle,
    modifier: Modifier = Modifier
) {
    val text = when {
        winner != null -> {
            val winnerName = if (winner == Player.WHITE) checkerStyle.lightName else checkerStyle.darkName
            "Победитель: $winnerName"
        }
        else -> {
            val playerName = if (currentPlayer == Player.WHITE) checkerStyle.lightName else checkerStyle.darkName
            "Ход: $playerName"
        }
    }

    Text(
        text = text,
        style = MaterialTheme.typography.headlineSmall,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = modifier
    )
}

@Composable
private fun CapturedPiecesCounter(
    whiteCaptured: Int,
    blackCaptured: Int,
    checkerStyle: CheckerStyle,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(
            text = "${checkerStyle.lightName} съели: $whiteCaptured",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = "${checkerStyle.darkName} съели: $blackCaptured",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}


@Composable
private fun BoardGrid(
    gameState: CheckersGameState,
    checkerStyle: CheckerStyle,
    cellSize: Dp,
    onPieceSelected: (Position) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(8),
        modifier = modifier.size(cellSize * 8),
        horizontalArrangement = Arrangement.spacedBy(0.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        items(64) { index ->
            val row = index / 8
            val col = index % 8
            val position = Position(row, col)
            val isBlackCell = (row + col) % 2 == 1
            val isSelected = gameState.selectedPiece == position
            val isPossibleMove = gameState.possibleMoves.any { it.to == position }
            val isCaptureMove = gameState.possibleMoves.any { it.to == position && it.captured != null }

            BoardCell(
                isBlackCell = isBlackCell,
                isSelected = isSelected,
                isPossibleMove = isPossibleMove,
                isCaptureMove = isCaptureMove,
                piece = gameState.getPieceAt(position),
                checkerStyle = checkerStyle,
                onClick = { onPieceSelected(position) },
                cellSize = cellSize
            )
        }
    }
}

@Composable
private fun BoardCell(
    isBlackCell: Boolean,
    isSelected: Boolean,
    isPossibleMove: Boolean,
    isCaptureMove: Boolean,
    piece: Piece?,
    checkerStyle: CheckerStyle,
    onClick: () -> Unit,
    cellSize: Dp
) {
    val cellColor = if (isBlackCell) Color(0xFF769656) else Color(0xFFEEEED2) // Классические шахматные цвета
    val highlightColor = Color(0x4076B6FF) // Голубой для выделения

    Box(
        modifier = Modifier
            .size(cellSize)
            .background(cellColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(cellSize * 0.9f)
                    .background(highlightColor)
            )
        }

        if (isPossibleMove) {
            val moveColor = if (isCaptureMove)
                Color(0x80FF0000)
            else
                Color(0x8000FF00)

            Box(
                modifier = Modifier
                    .size(cellSize * 0.3f)
                    .background(moveColor, CircleShape)
            )
        }

        piece?.let {
            CheckerPiece(
                piece = it,
                checkerStyle = checkerStyle,
                modifier = Modifier.size(cellSize * 0.8f)
            )
        }
    }
}

@Composable
fun CheckerPiece(
    piece: Piece,
    checkerStyle: CheckerStyle,
    modifier: Modifier = Modifier
) {
    val color = if (piece.player == Player.BLACK) checkerStyle.darkColor else checkerStyle.lightColor
    val borderColor = if (piece.player == Player.BLACK) checkerStyle.lightColor else checkerStyle.darkColor

    Box(
        modifier = modifier
            .background(color, CircleShape)
            .border(2.dp, borderColor, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (piece.type == PieceType.KING) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "King",
                tint = borderColor,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

