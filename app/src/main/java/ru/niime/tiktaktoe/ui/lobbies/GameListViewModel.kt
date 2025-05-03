package ru.niime.tiktaktoe.ui.lobbies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.niime.tiktaktoe.data.lobbies.GameRepository
import ru.niime.tiktaktoe.ui.common.ErrorMessage

class GameListViewModel(
    private val gameRepository: GameRepository
) : ViewModel() {
    private val _state = MutableStateFlow(GameListState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<GameListEffect>()
    val effect = _effect.asSharedFlow()

    fun loadGames() {
        if (_state.value.isLoading) return
        _state.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            gameRepository.getOpenGames().onSuccess { games ->
                _state.update { it.copy(games = games.map { it.toGameVO() }) }
            }.onFailure {
                _state.update { it.copy(error = ErrorMessage("Ошибка при загрузке")) }
            }
            _state.update { it.copy(isLoading = false) }
        }
    }

    fun onNavigateGame(id: String) {
        if (!_state.value.isCreatingGame) {
            viewModelScope.launch {
                _effect.emit(GameListEffect.NavigateGame(id))
            }
        }
    }

    fun createGame() {
        if (_state.value.isCreatingGame) return
        _state.update { it.copy(isCreatingGame = true) }
        viewModelScope.launch {
            gameRepository.createGame().onSuccess { gameId ->
                _effect.emit(GameListEffect.NavigateGame(gameId))
            }.onFailure {
                _state.update { it.copy(error = ErrorMessage("Ошибка при создании")) }
            }
            _state.update { it.copy(isCreatingGame = false) }
        }
    }
}