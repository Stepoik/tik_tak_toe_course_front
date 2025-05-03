package ru.niime.tiktaktoe.ui.lobbies

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import org.koin.compose.viewmodel.koinViewModel
import ru.niime.tiktaktoe.ui.AppRoute

@Composable
fun GameListScreen(navController: NavController) {
    val viewModel = koinViewModel<GameListViewModel>()
    val state = viewModel.state.collectAsState().value
    LaunchedEffect(Unit) {
        viewModel.loadGames()

        viewModel.effect.collect {
            when (it) {
                is GameListEffect.NavigateGame -> navController.navigate(AppRoute.Game(it.id))
            }
        }
    }
    when {
        state.isLoading -> {}
        state.error != null -> {
            Scaffold { Button(onClick = viewModel::loadGames, modifier = Modifier.padding(it)) {
                Text("Перезагрузить")
            } }
        }

        else -> {
            GameListScreen(
                lobbies = state.games,
                onJoinLobby = viewModel::onNavigateGame,
                onClickCreateGame = viewModel::createGame,
                onUpdate = viewModel::loadGames
            )
        }
    }
}

@Composable
fun GameListScreen(
    lobbies: List<GameVO>,
    onJoinLobby: (String) -> Unit,
    onClickCreateGame: () -> Unit,
    onUpdate: () -> Unit
) {
    Scaffold {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(16.dp)
        ) {
            Text(
                text = "Открытые лобби",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onClickCreateGame,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Создать игру", color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onUpdate,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Обновить список", color = Color.White)
            }

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(lobbies) { lobby ->
                    GameCard(lobby = lobby, onJoin = { onJoinLobby(lobby.id) })
                }
            }
        }
    }
}

@Composable
fun GameCard(lobby: GameVO, onJoin: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .background(Color.White)
            .padding(16.dp)
    ) {
        Text("Название: ${lobby.name}", fontSize = 16.sp, color = Color.DarkGray)

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onJoin,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Присоединиться", color = Color.White)
        }
    }
}