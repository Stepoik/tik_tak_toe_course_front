package ru.niime.tiktaktoe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import ru.niime.tiktaktoe.ui.AppRoute
import ru.niime.tiktaktoe.ui.game.GameScreenUi
import ru.niime.tiktaktoe.ui.lobbies.GameListScreen
import ru.niime.tiktaktoe.ui.theme.TikTakToeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TikTakToeTheme {
                val navController = rememberNavController()
                NavHost(navController, startDestination = AppRoute.Lobbies) {
                    composable<AppRoute.Lobbies> {
                        GameListScreen(navController)
                    }

                    composable<AppRoute.Game> {
                        val gameId = it.toRoute<AppRoute.Game>().id
                        GameScreenUi(navController, gameId)
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TikTakToeTheme {
        Greeting("Android")
    }
}