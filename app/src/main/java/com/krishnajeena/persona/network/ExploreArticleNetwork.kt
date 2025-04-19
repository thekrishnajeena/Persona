package com.krishnajeena.persona.network

import com.krishnajeena.persona.data_layer.DevToArticle
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface DevToApi {
    @GET("articles")
    suspend fun getArticlesByTag(
        @Query("tag") tag: String
    ): List<DevToArticle>
}

object RetrofitInstanceExploreArticle {
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://dev.to/api/")
            .addConverterFactory(GsonConverterFactory.create()) // or Moshi if preferred
            .build()
    }

    val api: DevToApi by lazy {
        retrofit.create(DevToApi::class.java)
    }
}
