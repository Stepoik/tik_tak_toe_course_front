package ru.niime.tiktaktoe.domain.models

data class Game(
    val id: String,
    val players: List<String>,
    val ready: List<Boolean>
)
