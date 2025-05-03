package ru.niime.tiktaktoe.ui.lobbies

import ru.niime.tiktaktoe.domain.models.Game

fun Game.toGameVO() = GameVO(
    id = id,
    name = players.firstOrNull() ?: ""
)