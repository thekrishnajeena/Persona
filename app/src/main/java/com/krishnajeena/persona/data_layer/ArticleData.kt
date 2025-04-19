package com.krishnajeena.persona.data_layer

import kotlinx.serialization.Serializable

@Serializable
data class DevToArticle(
    val id: Int,
    val title: String,
    val url: String,
    val cover_image: String? = null,
    val description: String? = null,
    val published_at: String
)
