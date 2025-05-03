package ru.niime.tiktaktoe.data.games

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

sealed interface GameEvent

@Serializable
data class GameStartEvent(
    @SerialName("this_player")
    val thisPlayer: String
) : GameEvent

@Serializable
data class GameUpdateEvent(
    val board: List<List<String>>,
    val turn: String
) : GameEvent

@Serializable
data class GameOverEvent(val winner: String?) : GameEvent

@Serializable
data class PlayerDisconnectedEvent(val playerId: String) : GameEvent