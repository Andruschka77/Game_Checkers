package com.example.game_checkers

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import kotlinx.coroutines.delay

@Composable
fun WelcomeScreen(
    onStartGame: () -> Unit,       // Обработчик начала игры
    onThemeChanged: (Boolean) -> Unit, // Обработчик изменения темы
    modifier: Modifier = Modifier  // Модификатор для размещения
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
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Кнопки выбора темы
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = { onThemeChanged(false) }) {
                Text("Светлая тема")
            }

            Button(onClick = { onThemeChanged(true) }) {
                Text("Тёмная тема")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Кнопка "Начать новую игру"
        Button(onClick = onStartGame) {
            Text("Начать новую игру")
        }
    }
}

@Composable
fun CheckersScreen(
    viewModel: CheckersViewModel,
    onBackPressed: () -> Unit,
    onGameFinished: (Player) -> Unit
) {
    val gameState by viewModel.gameState.collectAsState()

    if (gameState.winner != null) {
        LaunchedEffect(gameState.winner) {
            onGameFinished(gameState.winner!!)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Надпись "Ход: белые/чёрные" сверху с небольшим отступом
            CurrentPlayerIndicator(
                currentPlayer = gameState.currentPlayer,
                winner = gameState.winner,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Игровая доска
            BoardGrid(
                gameState = gameState,
                cellSize = minOf(
                    LocalConfiguration.current.screenWidthDp.dp,
                    LocalConfiguration.current.screenHeightDp.dp * 0.8f
                ) / 8,
                onPieceSelected = { position ->
                    viewModel.makeMove(position)
                    viewModel.selectPiece(position)// Вызов makeMove через ViewModel
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            CapturedPiecesCounter(
                whiteCaptured = gameState.getCapturedPiecesCount(Player.BLACK), // Белые съели чёрных
                blackCaptured = gameState.getCapturedPiecesCount(Player.WHITE), // Чёрные съели белых
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }

        // Кнопка "Назад" внизу по центру
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
fun WinnerScreen(
    winner: Player,
    onExit: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showScreen by remember { mutableStateOf(true) }

    LaunchedEffect(showScreen) {
        delay(3000)
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

@Composable
private fun BoardGrid(
    gameState: CheckersGameState,
    cellSize: Dp,
    onPieceSelected: (Position) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(8),
        modifier = modifier.size(cellSize * 8)
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
    onClick: () -> Unit,
    cellSize: Dp
) {
    Box(
        modifier = Modifier
            .size(cellSize)
            .background(if (isBlackCell) Color.DarkGray else Color.LightGray)
            .clickable(onClick = onClick)
    ) {
        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(cellSize * 0.8f)
                    .align(Alignment.Center)
                    .background(Color.Blue.copy(alpha = 0.5f))
            )
        }

        if (isPossibleMove) {
            Box(
                modifier = Modifier
                    .size(cellSize * 0.3f)
                    .align(Alignment.Center)
                    .background(if (isCaptureMove) Color.Red.copy(alpha = 0.5f) else Color.Green.copy(alpha = 0.5f))
            )
        }

        piece?.let {
            CheckerPiece(it, Modifier.size(cellSize * 0.8f).align(Alignment.Center))
        }
    }
}

@Composable
private fun CheckerPiece(piece: Piece, modifier: Modifier = Modifier) {
    val pieceColor = if (piece.player == Player.WHITE) Color.White else Color.Black
    val borderColor = if (piece.player == Player.WHITE) Color.Black else Color.White

    Box(
        modifier = modifier
            .background(pieceColor, CircleShape)
            .border(2.dp, borderColor, CircleShape)
    ) {
        if (piece.type == PieceType.KING) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "King",
                tint = borderColor,
                modifier = Modifier.size(24.dp).align(Alignment.Center)
            )
        }
    }
}

@Composable
private fun CurrentPlayerIndicator(
    currentPlayer: Player,
    winner: Player?,
    modifier: Modifier = Modifier
) {
    val text = when {
        winner != null -> "Победитель: ${if (winner == Player.WHITE) "Белые" else "Чёрные"}"
        else -> "Ход: ${if (currentPlayer == Player.WHITE) "Белые" else "Чёрные"}"
    }

    Text(
        text = text,
        style = MaterialTheme.typography.headlineMedium,
        color = MaterialTheme.colorScheme.onSurface, // Цвет текста зависит от темы
        textAlign = TextAlign.Center,
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
private fun CapturedPiecesCounter(whiteCaptured: Int, blackCaptured: Int, modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Text("Белые съели: $whiteCaptured", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.width(16.dp))
        Text("Чёрные съели: $blackCaptured", style = MaterialTheme.typography.bodyLarge)
    }
}