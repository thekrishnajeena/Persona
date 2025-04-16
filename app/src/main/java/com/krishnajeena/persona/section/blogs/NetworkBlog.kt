package com.krishnajeena.persona.section.blogs

import retrofit2.Retrofit
import retrofit2.Retrofit.*
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface BlogApiService {
    @GET("blogs.json")
    suspend fun getBlogs(): BlogCategoryResponse
}

object RetrofitClient {
    private val retrofit = Builder()
        .baseUrl("https://raw.githubusercontent.com/thekrishnajeena/KrishnaJeena/refs/heads/main/personadata/") // replace with your actual base URL
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: BlogApiService = retrofit.create(BlogApiService::class.java)
}
