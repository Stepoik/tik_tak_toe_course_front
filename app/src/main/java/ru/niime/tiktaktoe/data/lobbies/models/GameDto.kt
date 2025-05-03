package ru.niime.tiktaktoe.data.lobbies.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GameDto(
    @SerialName("game_id") val gameId: String,
    val players: List<String>,
    val ready: List<Boolean>,
    @SerialName("player_count") val playerCount: Int
)