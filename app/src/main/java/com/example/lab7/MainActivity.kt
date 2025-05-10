package com.example.lab7

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.compose.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.lab7.datasource.LocalDataSourceProvider
import com.example.lab7.ui.theme.Lab7Theme
import com.example.lab7.viewmodelrepo.AUTH_STATE
import com.example.lab7.viewmodelrepo.AuthViewModel
import com.example.lab7.viewmodelrepo.ChatViewModel


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "AppVariables")

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LocalDataSourceProvider.init(applicationContext.dataStore)
        enableEdgeToEdge()
        setContent {
            Lab7Theme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "login") {
                    composable("login") { LoginScreen(navController) }
                    composable("chat") { ChatScreen() }
                }
            }
        }
    }
}

@Composable
fun LoginScreen(navController: NavController, viewModel: AuthViewModel = viewModel()) {
    var email by remember { mutableStateOf("domic.rincon@gmail.com") }
    var password by remember { mutableStateOf("alfabeta") }

    val authState by viewModel.authState.collectAsState()

    if (authState.state == AUTH_STATE) {
        navController.navigate("chat") {
            launchSingleTop = true
        }
    }

    Column {
        Box(modifier = Modifier.height(200.dp))
        TextField(value = email, onValueChange = { email = it })
        TextField(value = password, onValueChange = { password = it })
        Button(onClick = { viewModel.login(email, password) }) {
            Text(text = "Iniciar sesión")
        }
    }
}

@Composable
fun ChatScreen(viewModel: ChatViewModel = viewModel()) {
    //TODO: Pantalla por realizar

    //Hagalo al principio al mostrar la pantalla
    LaunchedEffect(Unit) {
        viewModel.getLiveMessages()
    }

}