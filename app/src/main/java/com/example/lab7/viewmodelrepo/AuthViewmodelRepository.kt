package com.example.lab7.viewmodelrepo

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab7.datasource.AuthService
import com.example.lab7.datasource.LocalDataSourceProvider
import com.example.lab7.datasource.LoginDTO
import com.example.lab7.datasource.RetrofitConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

//******************************************************************************
//******************************************************************************
//******************************************************************************
class AuthViewModel(
    val authRepository: AuthRepository = AuthRepository()
) : ViewModel(){

    var authState:MutableStateFlow<AuthState> = MutableStateFlow( AuthState() )

    fun login(email:String, pass:String) {
        viewModelScope.launch(Dispatchers.IO) {
            authRepository.login(LoginDTO(
                email, pass
            ))
            authState.value = AuthState(state = AUTH_STATE)
        }
    }
}

var AUTH_STATE = "AUTH"
var NO_AUTH_STATE = "NO_AUTH"
var IDLE_AUTH_STATE = "IDLE_AUTH"

data class AuthState(
    var state:String = IDLE_AUTH_STATE
)

//******************************************************************************
//******************************************************************************
//******************************************************************************

class AuthRepository(
    val authService: AuthService = RetrofitConfig.directusRetrofit.create(AuthService::class.java)
) {
    suspend fun login(loginDTO: LoginDTO){
        val response = authService.login(loginDTO)
        LocalDataSourceProvider.get().save("accesstoken", response.data.access_token)

        var token = LocalDataSourceProvider.get().load("accesstoken").firstOrNull()
        Log.e(">>>", token.toString())
    }

    suspend fun getAccessToken() : String? {
        var token = LocalDataSourceProvider.get().load("accesstoken").firstOrNull()
        return token
    }
}

//******************************************************************************
//******************************************************************************
//******************************************************************************