package com.example.lab7.viewmodelrepo

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab7.datasource.AuthService
import com.example.lab7.datasource.LocalDataSource
import com.example.lab7.datasource.LocalDataSourceProvider
import com.example.lab7.datasource.MessageService
import com.example.lab7.datasource.RetrofitConfig
import com.example.lab7.datasource.live.Message
import com.example.lab7.datasource.live.MessagesLiveDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatViewModel(
    val chatRepository: ChatRepository = ChatRepository()
) : ViewModel() {

    val messagesState = MutableStateFlow<List<Message>>(listOf())

    fun getLiveMessages() {
        viewModelScope.launch(Dispatchers.IO) {
            chatRepository.observeMessages().collect { message ->
                Log.e(">>>VIEWMODEL", message.body)
                messagesState.update { currentList ->
                    currentList + message
                }
            }
        }
    }

    fun sendMessage(message: String) {
        viewModelScope.launch(Dispatchers.IO) {
            chatRepository.createMessage(message)
        }
    }

}

class ChatRepository(
    val messagesLiveDataSource: MessagesLiveDataSource = MessagesLiveDataSource(),
    val messageService: MessageService = RetrofitConfig.directusRetrofit.create(MessageService::class.java),
    val localDataSource: LocalDataSource = LocalDataSourceProvider.get()
) {

    suspend fun createMessage(message: String) {
        val token = localDataSource.load("accesstoken").first()
        val response = messageService.createMessage(
            "Bearer $token", Message(
                message,
                "Domiciano",
                "2025-02-02"
            )
        )
        response.body()
    }

    fun observeMessages(): Flow<Message> {
        return messagesLiveDataSource.observeMessages()
    }

}

