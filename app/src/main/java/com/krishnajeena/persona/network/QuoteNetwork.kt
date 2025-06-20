package com.krishnajeena.persona.network


import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.net.HttpURLConnection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

import java.net.URL

data class QuoteResponse(
    val quote: String
)

interface QuoteApi {
    @GET("quote")
    suspend fun getQuoteOfTheDay(): Response<QuoteResponse>
}

suspend fun fetchRemoteBaseUrl(): String? = withContext(Dispatchers.IO) {
    try {
        val url = URL("https://raw.githubusercontent.com/thekrishnajeena/KrishnaJeena/refs/heads/main/config_persona.json")
        val connection = url.openConnection() as HttpURLConnection
        connection.connect()

        val json = connection.inputStream.bufferedReader().readText()
        val config = JSONObject(json)
        config.getString("base_url")
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

object RetrofitClientQuote {

    private var quoteApi: QuoteApi? = null

    suspend fun getInstance(): QuoteApi {
        if (quoteApi == null) {
            val remoteUrl = fetchRemoteBaseUrl()
            val baseUrl = remoteUrl ?: "https://quote-api-one.vercel.app/" // fallback

            quoteApi = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(QuoteApi::class.java)
        }
        return quoteApi!!
    }
}

