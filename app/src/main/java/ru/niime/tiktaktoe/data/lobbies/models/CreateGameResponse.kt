package ru.niime.tiktaktoe.data.lobbies.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateGameResponse(
    @SerialName("game_id")
    val gameId: String
)
