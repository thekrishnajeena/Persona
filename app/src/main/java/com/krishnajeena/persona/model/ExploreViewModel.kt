package com.krishnajeena.persona.model

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krishnajeena.persona.data_layer.BlogCategory
import com.krishnajeena.persona.data_layer.BlogResponse
import com.krishnajeena.persona.network.RetrofitInstance
import com.krishnajeena.persona.other.NetworkMonitor
import io.ktor.client.engine.cio.CIO

import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.launch
import kotlinx.serialization.json.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.util.InternalAPI

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class ExploreViewModel(application: Application) : AndroidViewModel(application) {
    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            })
        }
    }

    private val networkMonitor = NetworkMonitor(application.applicationContext)

    private val _isConnected = MutableStateFlow(true)
    val isConnected: StateFlow<Boolean> = _isConnected

    var categories by mutableStateOf<List<BlogCategory>>(emptyList())
        private set

    var articlesCategories by mutableStateOf<List<String>>(emptyList())
        private set

    var isLoading by mutableStateOf(true)
        private set

    var firstSelected by mutableStateOf("economy")
        private set

    init {
        viewModelScope.launch {
            networkMonitor.isConnected.collect { connected ->
                _isConnected.value = connected
                if (connected && categories.isEmpty()) {
                    fetchCategories()
                    fetchArticlesCategories()
                }
            }
        }
    }

    fun fetchCategories() {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = RetrofitInstance.api.getCategories()
                categories = response.categories
            } catch (e: Exception) {
                Log.e("API_ERROR", "Error fetching categories: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }

    fun fetchArticlesCategories(){
        viewModelScope.launch{
            isLoading = true
            try {
                val response = RetrofitInstance.api.getArticlesCategories()
                articlesCategories = response.articlesCategories
                firstSelected = articlesCategories[0]
            } catch(e: Exception){
                Log.e("API_ERROR", "Error fetching articles categories: ${e.message}")
            } finally{
                isLoading = false
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        client.close()
        networkMonitor.unregisterCallback()
    }
}
