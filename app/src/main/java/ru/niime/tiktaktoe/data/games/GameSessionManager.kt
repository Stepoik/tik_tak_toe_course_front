package ru.niime.tiktaktoe.data.games

import ru.niime.tiktaktoe.data.network.NetworkConfig

class GameSessionManager {
    suspend fun connect(gameId: String, playerId: String): GameSession {
        val client = GameSocketClient(NetworkConfig.BASE_URL, gameId, playerId)
        client.connect()
        return GameSession(client)
    }
}