package com.example.game_checkers

enum class PieceType { REGULAR, KING }
enum class Player { WHITE, BLACK }

data class Piece(
    val player: Player, // Игрок, которому принадлежит шашка
    val type: PieceType = PieceType.REGULAR // Тип шашки: обычная или дамка
)
data class Position(val row: Int, val col: Int)
data class Move(val from: Position, val to: Position, val captured: Position? = null)

class CheckersGameState {
    private var board = Array(8) { arrayOfNulls<Piece?>(8) }
    var currentPlayer = Player.WHITE
    var selectedPiece: Position? = null
    var possibleMoves = emptyList<Move>()
    var winner: Player? = null
    var lastMove: Move? = null

    init {
        resetBoard()
    }

    fun resetBoard() {
        for (row in 0 until 8) {
            for (col in 0 until 8) {
                board[row][col] = when {
                    row < 3 && (row + col) % 2 == 1 -> Piece(Player.BLACK)
                    row > 4 && (row + col) % 2 == 1 -> Piece(Player.WHITE)
                    else -> null
                }
            }
        }
        currentPlayer = Player.WHITE
        selectedPiece = null
        possibleMoves = emptyList()
        winner = null
        lastMove = null
    }

    fun copy(
        board: Array<Array<Piece?>> = this.board,
        currentPlayer: Player = this.currentPlayer,
        selectedPiece: Position? = this.selectedPiece,
        possibleMoves: List<Move> = this.possibleMoves,
        winner: Player? = this.winner,
        lastMove: Move? = this.lastMove
    ): CheckersGameState {
        val newState = CheckersGameState()
        newState.board = board.map { it.copyOf() }.toTypedArray()
        newState.currentPlayer = currentPlayer
        newState.selectedPiece = selectedPiece
        newState.possibleMoves = possibleMoves
        newState.winner = winner
        newState.lastMove = lastMove
        return newState
    }

    fun getPieceAt(position: Position): Piece? {
        if (position.row !in 0..7 || position.col !in 0..7) return null
        return board[position.row][position.col]
    }

    fun selectPiece(position: Position) {
        if (winner != null) return

        val piece = getPieceAt(position) ?: return
        if (piece.player != currentPlayer) return

        selectedPiece = position
        possibleMoves = calculatePossibleMoves(position)
    }

    fun makeMove(move: Move): Boolean {
        if (winner != null) return false
        if (move !in possibleMoves) return false

        val piece = getPieceAt(move.from)!!
        board[move.from.row][move.from.col] = null

        // Превращение в дамку
        val newType = when {
            piece.type == PieceType.KING -> PieceType.KING
            move.to.row == 0 && piece.player == Player.WHITE -> PieceType.KING
            move.to.row == 7 && piece.player == Player.BLACK -> PieceType.KING
            else -> PieceType.REGULAR
        }

        board[move.to.row][move.to.col] = Piece(piece.player, newType)
        lastMove = move

        // Удаление съеденной шашки
        move.captured?.let { captured ->
            board[captured.row][captured.col] = null

            // Проверка на возможность дальнейшего взятия
            val nextCaptures = calculatePossibleMoves(move.to).filter { it.captured != null }
            if (nextCaptures.isNotEmpty()) {
                selectedPiece = move.to
                possibleMoves = nextCaptures
                return true
            }
        }

        switchPlayer()
        checkForWinner()
        return true
    }

    private fun checkForWinner() {
        val whitePieces = board.flatten().count { it?.player == Player.WHITE }
        val blackPieces = board.flatten().count { it?.player == Player.BLACK }

        winner = when {
            whitePieces == 0 -> Player.BLACK // Победили чёрные
            blackPieces == 0 -> Player.WHITE // Победили белые
            currentPlayerHasNoMoves() -> if (currentPlayer == Player.WHITE) Player.BLACK else Player.WHITE
            else -> null
        }
    }

    private fun switchPlayer() {
        currentPlayer = if (currentPlayer == Player.WHITE) Player.BLACK else Player.WHITE
        selectedPiece = null
        possibleMoves = emptyList()

        checkWinCondition()
    }

    private fun checkWinCondition() {
        val whitePieces = board.flatten().count { it?.player == Player.WHITE }
        val blackPieces = board.flatten().count { it?.player == Player.BLACK }

        winner = when {
            whitePieces == 0 -> Player.BLACK
            blackPieces == 0 -> Player.WHITE
            currentPlayerHasNoMoves() -> if (currentPlayer == Player.WHITE) Player.BLACK else Player.WHITE
            else -> null
        }
    }

    private fun currentPlayerHasNoMoves(): Boolean {
        for (row in 0 until 8) {
            for (col in 0 until 8) {
                val pos = Position(row, col)
                if (getPieceAt(pos)?.player == currentPlayer && calculatePossibleMoves(pos).isNotEmpty()) {
                    return false
                }
            }
        }
        return true
    }

    private fun calculatePossibleMoves(position: Position): List<Move> {
        val piece = getPieceAt(position) ?: return emptyList()
        val moves = mutableListOf<Move>()

        val directions = when {
            piece.type == PieceType.KING -> listOf(-1 to -1, -1 to 1, 1 to -1, 1 to 1)
            piece.player == Player.WHITE -> listOf(-1 to -1, -1 to 1)
            else -> listOf(1 to -1, 1 to 1)
        }

        for ((dr, dc) in directions) {
            val newRow = position.row + dr
            val newCol = position.col + dc

            if (newRow in 0..7 && newCol in 0..7 && getPieceAt(Position(newRow, newCol)) == null) {
                moves.add(Move(position, Position(newRow, newCol)))
            } else if (newRow in 0..7 && newCol in 0..7) {
                val capturedPiece = getPieceAt(Position(newRow, newCol))
                if (capturedPiece != null && capturedPiece.player != piece.player) {
                    val jumpRow = newRow + dr
                    val jumpCol = newCol + dc
                    if (jumpRow in 0..7 && jumpCol in 0..7 && getPieceAt(Position(jumpRow, jumpCol)) == null) {
                        moves.add(Move(position, Position(jumpRow, jumpCol), Position(newRow, newCol)))
                    }
                }
            }
        }

        if (piece.type == PieceType.KING) {
            for ((dr, dc) in directions) {
                var currentRow = position.row + dr
                var currentCol = position.col + dc
                var capturePos: Position? = null

                while (currentRow in 0..7 && currentCol in 0..7) {
                    val currentPos = Position(currentRow, currentCol)
                    val currentPiece = getPieceAt(currentPos)

                    when {
                        currentPiece == null -> {
                            if (capturePos == null) {
                                moves.add(Move(position, currentPos))
                            } else {
                                moves.add(Move(position, currentPos, capturePos))
                            }
                        }
                        currentPiece.player == piece.player -> break
                        capturePos != null -> break
                        else -> capturePos = currentPos
                    }

                    currentRow += dr
                    currentCol += dc
                }
            }
        }

        val captures = moves.filter { it.captured != null }
        return if (captures.isNotEmpty()) captures else moves
    }

    fun getCapturedPiecesCount(player: Player): Int {
        val totalPieces = if (player == Player.WHITE) 12 else 12 // Количество шашек игрока в начале игры
        val remainingPieces = board.flatten().count { it?.player == player } // Количество оставшихся шашек игрока
        return totalPieces - remainingPieces // Количество съеденных шашек
    }
}