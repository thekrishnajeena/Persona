package com.krishnajeena.persona.data_layer

import kotlinx.serialization.Serializable



@Serializable
data class BlogResponse(
    val categories: List<BlogCategory>
)


@Serializable
data class BlogCategory(
    val name: String,
    val image: String = "", // optional; leave blank if not present
    val blogs: List<BlogItem>
)

@Serializable
data class BlogItem(
    val title: String,
    val url: String
)

