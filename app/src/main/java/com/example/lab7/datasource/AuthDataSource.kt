package com.example.lab7.datasource

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

//******************************************************************************
//******************************************************************************
//******************************************************************************

interface AuthService {
    @POST("/auth/login")
    suspend fun login(@Body loginDTO: LoginDTO) : LoginResponse

    @GET("/users")
    suspend fun getAllUsers(@Header("Authorization") authorization:String) : UserResponse
}

//******************************************************************************
//******************************************************************************
//******************************************************************************

data class LoginResponse(
    val data: LoginResponseData
)

data class LoginResponseData(
    val access_token:String,
    val refresh_token:String,
)

data class LoginDTO(
    val email:String,
    val password:String
)

data class UserResponse(
    val data : List<UserDTO>
)

data class UserDTO(
    val first_name:String,
    val last_name:String,
    val email: String
)

//******************************************************************************
//******************************************************************************
//******************************************************************************

object RetrofitConfig {
    val directusRetrofit: Retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8055")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}