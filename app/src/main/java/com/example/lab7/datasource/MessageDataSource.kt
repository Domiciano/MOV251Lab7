package com.example.lab7.datasource

import com.example.lab7.datasource.live.Message
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface MessageService {


    @POST("/items/messages")
    suspend fun createMessage(@Header("Authorization") authorization:String, @Body message:Message) : Response<Any>



}
