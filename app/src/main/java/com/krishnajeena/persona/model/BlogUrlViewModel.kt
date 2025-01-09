package com.krishnajeena.persona.model

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krishnajeena.persona.data_layer.BlogUrl
import com.krishnajeena.persona.data_layer.BlogUrlDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BlogUrlViewModel @Inject constructor(
    blogUrlDatabase: BlogUrlDatabase,
   // private val blogUrlRepository: BlogUrlRepository
): ViewModel() {

    val dao = blogUrlDatabase.blogUrlDao
    val urls = mutableStateListOf<BlogUrl>()

    init{
        viewModelScope.launch {
           urls.addAll(dao.getAllUrls())
            //urls.addAll(blogUrlRepository.getAllUrls())
        }
    }

    fun addUrl(blogName: String, blogUrl: String){
        viewModelScope.launch {
            val newBlogUrl = BlogUrl(name = blogName, url = blogUrl)
            dao.insertUrl(newBlogUrl)
            urls.add(newBlogUrl)
        }
    }

    fun removeUrl(blogUrl: BlogUrl){
        viewModelScope.launch {
            dao.deleteUrl(blogUrl)
            urls.remove(blogUrl)
        }
    }

    fun isEmpty(): Boolean{
        return urls.isEmpty()
    }

}