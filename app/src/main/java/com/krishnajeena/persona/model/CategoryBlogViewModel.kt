package com.krishnajeena.persona.model

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.krishnajeena.persona.data_layer.BlogItem

class CategoryBlogViewModel : ViewModel() {
    private val _selectedBlogs = mutableStateOf<List<BlogItem>>(emptyList())
    val selectedBlogs = _selectedBlogs

    private val _blogCategoryName = mutableStateOf<String>("Persona")
    val blogCategoryName = _blogCategoryName

    fun setBlogs(blogs: List<BlogItem>) {
        _selectedBlogs.value = blogs
    }

    fun setName(name: String){
        _blogCategoryName.value = name
    }
}
