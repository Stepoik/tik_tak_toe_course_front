package ru.niime.tiktaktoe.data.lobbies

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import ru.niime.tiktaktoe.data.lobbies.models.CreateGameResponse
import ru.niime.tiktaktoe.data.lobbies.models.GameDto
import ru.niime.tiktaktoe.data.network.NetworkConfig
import ru.niime.tiktaktoe.domain.models.Game

class GameRepository(private val client: HttpClient) {
    suspend fun getOpenGames(): Result<List<Game>> {
        return runCatching {
            val dtoList: List<GameDto> = client.get("${NetworkConfig.BASE_URL}/games").body()
            dtoList.map {
                Game(
                    id = it.gameId,
                    players = it.players,
                    ready = it.ready
                )
            }
        }
    }

    suspend fun createGame(): Result<String> {
        return runCatching {
            client.post("${NetworkConfig.BASE_URL}/games").body<CreateGameResponse>().gameId
        }
    }
}