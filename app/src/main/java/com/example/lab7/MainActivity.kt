package com.example.lab7

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
import androidx.compose.material3.CardElevation
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp


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

    val messages by viewModel.messagesState.collectAsState()

    var message by remember { mutableStateOf("") }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(messages) { messagei ->
                    MessageCard(text = messagei.body, author = messagei.author)
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextField(
                    modifier = Modifier.weight(1f),
                    value = message,
                    onValueChange = { message = it })
                Box(modifier = Modifier.width(8.dp))
                Button(onClick = {
                    viewModel.sendMessage(message)
                    message = ""
                }) {
                    Text(text = "Send")
                }
            }
        }
    }
}

@Composable
fun MessageCard(text: String, author: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF512DA8)
        ),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "$author dice", fontSize = 10.sp, color = Color.White)
            Text(text = text, fontSize = 18.sp, color = Color.White)
        }

    }
}