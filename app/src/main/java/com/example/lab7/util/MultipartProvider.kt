package com.example.lab7.util

import android.content.Context
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.UUID

object MultipartProvider {

    private var instance: FileSource? = null

    fun init(context: Context) {
        if (instance == null) {
            instance = FileSource(context)
        }
    }

    fun get(): FileSource {
        return instance ?: throw IllegalStateException("LocalDataStore is not initialized")
    }

}

class FileSource(val context: Context) {

    fun prepareMultipartFromUri(uri: Uri): MultipartBody.Part {
        val contentResolver = context.contentResolver
        val inputStream = contentResolver.openInputStream(uri) ?: throw IOException("invalid URI")
        val fileName = UUID.randomUUID().toString()
        val mimeType = contentResolver.getType(uri) ?: "application/octet-stream"
        val requestBody = inputStream.readBytes().toRequestBody(mimeType.toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("file", fileName, requestBody)
    }

}
