package com.krishnajeena.persona.network
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Headers

data class VideoResponse(
    val url: String?,
    val ext: String?,
    val resolution: String?,
    val fps: String?,  // Change from Int? to String?
    val filesize: String?,  // Change from Long? to String?
    val error: String?
)


interface VideoApiService {
    @Headers("Content-Type: application/json")
    @POST("/get_video_url")
    suspend fun getVideo(@Body request: Map<String, String>): VideoResponse
}

object RetrofitClient {
    private const val BASE_URL = "https://www.downloadnow.space/"

    val instance: VideoApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(VideoApiService::class.java)
    }
}
