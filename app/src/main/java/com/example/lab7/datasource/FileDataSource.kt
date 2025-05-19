package com.example.lab7.datasource

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface FileDataSource {
    @Multipart
    @POST("files")
    suspend fun uploadFile(
        @Header("Authorization") authHeader: String,
        @Part file: MultipartBody.Part
    ): Response<FileUploadResponse>

    @PATCH("files/{id}")
    suspend fun updateFileMetadata(
        @Header("Authorization") auth: String,
        @Path("id") fileId: String,
        @Body body: FileUpdateRequest
    ): Response<Any>
}

data class FileUploadResponse(
    val data:FileUploadResponseData
)
data class FileUploadResponseData(
    val id:String,
    val filename_download:String
)

data class FileUpdateRequest(
    val title:String,
    val filename_download:String
)