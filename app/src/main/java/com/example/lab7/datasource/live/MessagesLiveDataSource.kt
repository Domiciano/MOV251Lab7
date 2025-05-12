package com.example.lab7.datasource.live

import android.util.Log
import com.example.lab7.datasource.LocalDataSource
import com.example.lab7.datasource.LocalDataSourceProvider
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject
import retrofit2.http.Body
import java.sql.Timestamp

class MessagesLiveDataSource {

    val messageFlow = MutableSharedFlow<Message>()

    fun observeMessages() : Flow<Message> {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("ws://10.0.2.2:8055/websocket")
            .build()
        //1. Conexion
        client.newWebSocket(
            request,
            MessagesWebSocketListener(flow = messageFlow)
        )
        return messageFlow
    }
}

class MessagesWebSocketListener(
    val localDataSource: LocalDataSource = LocalDataSourceProvider.get(),
    val flow:MutableSharedFlow<Message>
) : WebSocketListener() {

    override fun onOpen(webSocket: WebSocket, response: Response) {
        //Esto se ejecuta apenas me conecto
        val token = runBlocking {  localDataSource.load("accesstoken").first() }

        val authMessage = """
        {
            "type":"auth", 
            "access_token":"$token"
        }
        """.trimIndent()
        webSocket.send(authMessage)

    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        val json = JSONObject(text)
        val type = json.optString("type")
        when(type){
            "auth" -> {
                Log.e(">>>", "Auth $text")
                val queryMessage = """
                    {
                        "type":"subscribe", 
                        "collection":"messages", 
                        "query":{
                            "limit":10
                        },
                        "sort": "['-timestamp']"
                    }
                """.trimIndent()
                webSocket.send(queryMessage)
            }
            "ping" -> {
                Log.e(">>>", "Recibo ping")

                val pong = """
                    {"type":"pong"}
                """.trimIndent()
                webSocket.send(pong)

                Log.e(">>>", "Respongo con pong")
            }
            "subscription" -> {
                Log.e(">>>SUBS", text)
                val subs = Gson().fromJson(text, Subscription::class.java)

                subs.data.forEach {
                    runBlocking {  flow.emit(it) }
                }

            }
        }
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        Log.e(">>>", "Cerrando WebSocket: $code > $reason")
        webSocket.close(1000, null)
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        Log.e(">>>", "Error WebSocket: ${t.message}")
    }
}

data class Subscription(
    val type: String,
    val event: String,
    val data : List<Message>
)

data class Message(
    val body: String,
    val author: String,
    val timestamp: String
)