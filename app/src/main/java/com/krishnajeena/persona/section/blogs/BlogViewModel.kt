package com.krishnajeena.persona.section.blogs

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class BlogViewModel : ViewModel() {
    var blogCategories by mutableStateOf<List<BlogCategory>>(emptyList())
        private set

    var isLoading by mutableStateOf(true)
        private set

    init {
        fetchBlogs()
    }

    private fun fetchBlogs() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.api.getBlogs()
                blogCategories = response.categories
            } catch (e: Exception) {
                Log.e("BlogViewModel", "Error fetching blogs: ${e.localizedMessage}")
            } finally {
                isLoading = false
            }
        }
    }
}
