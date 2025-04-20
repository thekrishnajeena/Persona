package com.krishnajeena.persona.network

import com.krishnajeena.persona.data_layer.ArticleCategoriesResponse
import com.krishnajeena.persona.data_layer.BlogResponse
import retrofit2.http.GET

interface BlogApiService {
    @GET("thekrishnajeena/KrishnaJeena/refs/heads/main/personadata/categoryBlogs.json")
    suspend fun getCategories(): BlogResponse

    @GET("thekrishnajeena/KrishnaJeena/refs/heads/main/personadata/categoryBlogs.json")
    suspend fun getArticlesCategories(): ArticleCategoriesResponse
}

object RetrofitInstance {
    private const val BASE_URL = "https://raw.githubusercontent.com/"

    val api: BlogApiService by lazy {
        retrofit2.Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
            .build()
            .create(BlogApiService::class.java)
    }
}
