package ru.niime.tiktaktoe.ui.game

data class GameState(
    val thisPlayer: Player? = null,
    val board: List<List<String>> = listOf(),  // 3x3: "", "X", "O"
    val currentPlayer: Player? = null,      // "X" или "O"
    val result: GameResult? = null,
    val isConnected: Boolean = false
)

sealed class GameResult {
    data object Draw : GameResult()
    data class Winner(val winner: Player) : GameResult()
}

enum class Player {
    X, O
}

sealed class GameEffect {
    data object NavigateLobbies : GameEffect()
}