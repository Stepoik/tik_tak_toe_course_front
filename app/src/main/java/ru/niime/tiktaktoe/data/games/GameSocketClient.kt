package ru.niime.tiktaktoe.data.games

import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.client.request.url
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

@Serializable
data class GameMessage(
    val type: String,
    val data: JsonObject
)

@Serializable
data class MovePayload(val row: Int, val col: Int)

@Serializable
data class GameStartPayload(val turn: String)

class GameSocketClient(
    private val baseUrl: String,
    private val gameId: String,
    private val playerId: String,
    private val reconnectDelayMs: Long = 3000,
    private val reconnectAttempts: Int = 5,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
) {
    private val client = HttpClient {
        install(WebSockets)
    }

    private var session: WebSocketSession? = null
    private var receiveJob: Job? = null
    private var reconnectJob: Job? = null

    private val json = Json { ignoreUnknownKeys = true }

    private val _events = MutableSharedFlow<GameEvent>()
    val events: SharedFlow<GameEvent> = _events.asSharedFlow()

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    private val wsUrl = "$baseUrl/ws/$gameId/$playerId".replace("http", "ws")

    suspend fun connect() {
        if (_isConnected.value) return

        try {
            val wsSession = client.webSocketSession {
                url(wsUrl)
            }

            session = wsSession
            _isConnected.value = true

            receiveJob = scope.launch {
                try {
                    for (frame in wsSession.incoming) {
                        if (frame is Frame.Text) {
                            handleFrame(frame.readText())
                        }
                    }
                } catch (e: Exception) {
                    println("Receive error: ${e.message}")
                } finally {
                    _isConnected.value = false
                    tryReconnect()
                }
            }

        } catch (e: Exception) {
            println("Initial connect failed: ${e.message}")
            _isConnected.value = false
            tryReconnect()
        }
    }

    private suspend fun handleFrame(text: String) {
        try {
            val jsonObject = json.parseToJsonElement(text).jsonObject
            val type = jsonObject["type"]?.jsonPrimitive?.content ?: return
            val data = jsonObject["data"] ?: return

            val event = when (type) {
                "game_start" -> json.decodeFromJsonElement<GameStartEvent>(data)
                "game_update" -> json.decodeFromJsonElement<GameUpdateEvent>(data)
                "game_over" -> json.decodeFromJsonElement<GameOverEvent>(data)
                "player_disconnected" -> json.decodeFromJsonElement<PlayerDisconnectedEvent>(data)
                else -> null
            }

            if (event != null) {
                _events.emit(event)
            }
        } catch (e: Exception) {
            println("Parse error: ${e.message}")
        }
    }

    private fun tryReconnect() {
        if (reconnectJob?.isActive == true) return

        reconnectJob = scope.launch {
            repeat(reconnectAttempts) { attempt ->
                delay(reconnectDelayMs)
                println("Reconnecting... (attempt ${attempt + 1})")
                try {
                    connect()
                    if (_isConnected.value) {
                        println("Reconnected.")
                        return@launch
                    }
                } catch (e: Exception) {
                    println("Reconnect failed: ${e.message}")
                }
            }
            println("Max reconnect attempts reached.")
        }
    }

    suspend fun sendReady() {
        sendMessage("ready", buildJsonObject { })
    }

    suspend fun sendMove(row: Int, col: Int) {
        val move = buildJsonObject {
            put("row", JsonPrimitive(row))
            put("col", JsonPrimitive(col))
        }
        sendMessage("move", move)
    }

    private suspend fun sendMessage(type: String, data: JsonObject) {
        val message = buildJsonObject {
            put("type", JsonPrimitive(type))
            put("data", data)
        }
        val text = json.encodeToString(JsonObject.serializer(), message)

        try {
            session?.send(Frame.Text(text))
        } catch (e: Exception) {
            println("Send error: ${e.message}")
        }
    }

    suspend fun disconnect() {
        reconnectJob?.cancel()
        receiveJob?.cancel()
        session?.close()
        session = null
        _isConnected.value = false
    }
}