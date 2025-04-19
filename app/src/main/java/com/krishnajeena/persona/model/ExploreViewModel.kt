package com.krishnajeena.persona.model

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krishnajeena.persona.data_layer.BlogCategory
import com.krishnajeena.persona.data_layer.BlogResponse
import com.krishnajeena.persona.network.RetrofitInstance
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


class ExploreViewModel : ViewModel() {
    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            })
        }
    }


    var categories by mutableStateOf<List<BlogCategory>>(emptyList())
        private set

    var isLoading by mutableStateOf(true)
        private set

    init {
        Log.i("TAG::::", "Inside Init")
        fetchCategories()
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

    override fun onCleared() {
        super.onCleared()
        client.close()
    }
}
