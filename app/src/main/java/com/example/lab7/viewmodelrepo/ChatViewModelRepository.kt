package com.example.lab7.viewmodelrepo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab7.datasource.live.MessagesLiveDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatViewModel(
    val chatRepository: ChatRepository = ChatRepository()
):ViewModel(){

    fun getLiveMessages() {
        viewModelScope.launch (Dispatchers.IO){
            chatRepository.observeMessages()
        }
    }

}

class ChatRepository(
    val messagesLiveDataSource: MessagesLiveDataSource = MessagesLiveDataSource()
){
    fun observeMessages() {
        messagesLiveDataSource.observeMessages()
    }

}

