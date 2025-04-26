package com.krishnajeena.persona.network


import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET


data class QuoteResponse(
    val quote: String
)

interface QuoteApi {
    @GET("quote")
    suspend fun getQuoteOfTheDay(): Response<QuoteResponse>
}

object RetrofitClientQuote {
    private const val BASE_URL = "https://quotepersona-production.up.railway.app/" // Your deployed Flask URL

    val instance: QuoteApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(QuoteApi::class.java)
    }
}
