package com.krishnajeena.persona.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krishnajeena.persona.data_layer.DevToArticle
import com.krishnajeena.persona.network.RetrofitInstance
import com.krishnajeena.persona.network.RetrofitInstanceExploreArticle
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.launch

class ArticlesViewModel : ViewModel() {


    var articles by mutableStateOf(listOf<DevToArticle>())
    var isLoading by mutableStateOf(false)
    var selectedCategory by mutableStateOf("entrepreneurship")

    fun fetchArticles(tag: String) {
        selectedCategory = tag
        viewModelScope.launch {
            isLoading = true
            try {
                val response = RetrofitInstanceExploreArticle.api.getArticlesByTag(tag)
                articles = response
            } catch (e: Exception) {
                e.printStackTrace()
                articles = emptyList()
            } finally {
                isLoading = false
            }
        }
    }

    fun onCategoryClick(tag: String) {
        selectedCategory = tag
        fetchArticles(tag)
    }
}
