package ru.niime.tiktaktoe.ui

import kotlinx.serialization.Serializable

@Serializable
sealed class AppRoute {
    @Serializable
    data object Lobbies : AppRoute()
    @Serializable
    data class Game(val id: String) : AppRoute()
}