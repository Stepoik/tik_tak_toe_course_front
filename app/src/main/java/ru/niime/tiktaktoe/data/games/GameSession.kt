package ru.niime.tiktaktoe.data.games

import kotlinx.coroutines.flow.Flow

class GameSession(
    private val client: GameSocketClient
) {

    val events: Flow<GameEvent> = client.events

    suspend fun sendReady() = client.sendReady()

    suspend fun sendMove(row: Int, col: Int) = client.sendMove(row, col)

    suspend fun disconnect() = client.disconnect()
}