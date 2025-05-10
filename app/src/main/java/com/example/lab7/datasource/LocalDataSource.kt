package com.example.lab7.datasource

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object LocalDataSourceProvider{

    private var instance: LocalDataSource? = null

    fun init(dataStore: DataStore<Preferences>){
        if(instance == null){
            instance = LocalDataSource(dataStore)
        }
    }

    fun get():LocalDataSource{
        return instance ?: throw IllegalStateException("LocalDataStore is not initialized")
    }

}

class LocalDataSource(val dataStore: DataStore<Preferences>) {

    suspend fun save(key:String, value:String){
        dataStore.edit { prefs ->
            prefs[stringPreferencesKey(key)] = value
        }
    }

    fun load(key:String): Flow<String> = dataStore.data.map { prefs ->
        prefs[stringPreferencesKey(key)] ?: ""
    }

}