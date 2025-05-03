package ru.niime.tiktaktoe.ui.game

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.niime.tiktaktoe.data.games.GameOverEvent
import ru.niime.tiktaktoe.data.games.GameSession
import ru.niime.tiktaktoe.data.games.GameSessionManager
import ru.niime.tiktaktoe.data.games.GameStartEvent
import ru.niime.tiktaktoe.data.games.GameUpdateEvent
import ru.niime.tiktaktoe.data.games.PlayerDisconnectedEvent
import ru.niime.tiktaktoe.data.users.UserRepository

class GameViewModel(
    private val gameId: String,
    private val gameSessionManager: GameSessionManager,
    private val userRepository: UserRepository
) : ViewModel() {
    private var session: GameSession? = null

    private val _state = MutableStateFlow(GameState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<GameEffect>()
    val effect = _effect.asSharedFlow()

    init {
        viewModelScope.launch {
            session =
                gameSessionManager.connect(gameId = gameId, playerId = userRepository.getUserId())
            session?.let {
                observeEvents(it)
            }
        }
    }

    fun onCellClick(row: Int, col: Int) {
        viewModelScope.launch { session?.sendMove(row, col) }
    }

    fun exit() {
        viewModelScope.launch {
            _effect.emit(GameEffect.NavigateLobbies)
        }
    }

    fun sendReady() {
        viewModelScope.launch {
            session?.sendReady()
        }
    }

    private suspend fun observeEvents(session: GameSession) {
        session.events.collect { event ->
            when (event) {
                is GameStartEvent -> {
                    _state.update {
                        it.copy(
                            currentPlayer = Player.X,
                            thisPlayer = event.thisPlayer.toPlayer(),
                            board = List(3, { List(3, { "" }) })
                        )
                    }
                }

                is GameUpdateEvent -> {
                    val currentPlayer = event.turn.toPlayer()
                    _state.update { it.copy(board = event.board, currentPlayer = currentPlayer) }
                }

                is GameOverEvent -> {
                    val gameResult = when (val winPlayer = event.winner?.toPlayer()) {
                        null -> GameResult.Draw
                        else -> GameResult.Winner(winPlayer)
                    }
                    _state.update { it.copy(result = gameResult) }
                }

                is PlayerDisconnectedEvent -> {

                }
            }
        }
    }
}

private fun String.toPlayer(): Player? {
    return when (this) {
        "X" -> Player.X
        "O" -> Player.O
        else -> null
    }
}