package com.krishnajeena.persona.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

data class Book(
    val id: String,
    val title: String,
    val author: String,
    val file_url: String
)

interface BookApi{
    @GET("/books")
    suspend fun getBooks(): List<Book>
}

object BookRetrofitInstance {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://example.com")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: BookApi = retrofit.create(BookApi::class.java)
}