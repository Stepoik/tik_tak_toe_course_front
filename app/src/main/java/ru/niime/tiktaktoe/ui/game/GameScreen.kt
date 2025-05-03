package ru.niime.tiktaktoe.ui.game

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import ru.niime.tiktaktoe.ui.AppRoute

@Composable
fun GameScreenUi(navController: NavController, gameId: String) {
    val viewModel = koinViewModel<GameViewModel>(parameters = { parametersOf(gameId) })
    val state = viewModel.state.collectAsState().value
    LaunchedEffect(Unit) {
        viewModel.effect.collect {
            when (it) {
                is GameEffect.NavigateLobbies -> navController.navigate(AppRoute.Lobbies)
            }
        }
    }
    if (state.currentPlayer != null && state.thisPlayer != null) {
        GameScreenUi(
            board = state.board,
            thisPlayer = state.thisPlayer,
            currentPlayer = state.currentPlayer,
            gameResult = state.result,
            onCellClick = viewModel::onCellClick,
            onExit = viewModel::exit
        )
    } else {
        Scaffold {
            Button(onClick = viewModel::sendReady, modifier = Modifier.padding(it)) {
                Text("Я готов")
            }
        }
    }
}

@Composable
fun GameScreenUi(
    board: List<List<String>>,
    thisPlayer: Player,
    currentPlayer: Player,
    gameResult: GameResult?,
    onCellClick: (row: Int, col: Int) -> Unit,
    onExit: () -> Unit
) {
    Scaffold {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .background(Color(0xFFECEFF1))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Ходит: $currentPlayer",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF212121)
            )

            Spacer(modifier = Modifier.height(24.dp))

            for (row in 0..2) {
                Row {
                    for (col in 0..2) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(80.dp)
                                .border(1.dp, Color.Black)
                                .background(Color.White)
                                .clickable { onCellClick(row, col) }
                        ) {
                            Text(
                                text = board[row][col],
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = when (board[row][col]) {
                                    "X" -> Color(0xFF1976D2)
                                    "O" -> Color(0xFFD32F2F)
                                    else -> Color.Transparent
                                },
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Вы ${thisPlayer.name}",
                fontSize = 24.sp,
                color = Color(0xFF388E3C)
            )

            Spacer(modifier = Modifier.height(24.dp))

            when(gameResult) {
                is GameResult.Winner -> {
                    Text(
                        text = "Победил: ${gameResult.winner}",
                        fontSize = 22.sp,
                        color = Color(0xFF388E3C)
                    )
                }
                is GameResult.Draw -> {
                    Text(
                        text = "Ничья!",
                        fontSize = 22.sp,
                        color = Color(0xFF757575)
                    )
                }

                null -> {}
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (gameResult != null) {
                Button(
                    onClick = onExit,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF009688))
                ) {
                    Text("Выйти", color = Color.White)
                }
            }
        }
    }
}