package com.example.lab7.viewmodelrepo

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab7.datasource.FileDataSource
import com.example.lab7.datasource.FileUpdateRequest
import com.example.lab7.datasource.LocalDataSource
import com.example.lab7.datasource.LocalDataSourceProvider
import com.example.lab7.datasource.RetrofitConfig
import com.example.lab7.util.MultipartProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


class ProfileViewModel(
    val profileRepo: ProfileRepo = ProfileRepo()
) : ViewModel() {
    var urlImage =
        MutableStateFlow("https://raw.githubusercontent.com/Domiciano/AppMoviles251/refs/heads/main/res/images/Lab4Cover.png")

    fun uploadImage(image: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            profileRepo.uploadImage(image)?.let { imageId ->
                urlImage.value = "http://10.0.2.2:8055/assets/$imageId"
            }
        }
    }
}

class ProfileRepo(
    val fileDataSource: FileDataSource = RetrofitConfig.directusRetrofit.create(
        FileDataSource::class.java
    )
) {
    suspend fun uploadImage(image: Uri):String? {
        //Uri -> Multipart.Body
        val mp = MultipartProvider.get().prepareMultipartFromUri(image);
        val localDataSource: LocalDataSource = LocalDataSourceProvider.get()
        val token = localDataSource.load("accesstoken").first()
        val response = fileDataSource.uploadFile("Bearer $token", mp)
        response.body()?.let {
            Log.e(">>>", it.data.id)
            val response = fileDataSource.updateFileMetadata(
                "Bearer $token",
                    it.data.id,
                    FileUpdateRequest(
                        it.data.id,
                        it.data.id
                    )
            )
            Log.e(">>>", response.code().toString())
            if(response.code() == 200){
                return it.data.id
            }

        }
        return null
    }

}
