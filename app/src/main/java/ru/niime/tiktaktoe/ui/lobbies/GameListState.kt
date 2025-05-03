package ru.niime.tiktaktoe.ui.lobbies

import ru.niime.tiktaktoe.ui.common.ErrorMessage

data class GameListState(
    val isLoading: Boolean = false,
    val games: List<GameVO> = listOf(),
    val error: ErrorMessage? = null,
    val isCreatingGame: Boolean = false
)

data class GameVO(
    val id: String,
    val name: String
)

sealed class GameListEffect {
    data class NavigateGame(val id: String) : GameListEffect()
}