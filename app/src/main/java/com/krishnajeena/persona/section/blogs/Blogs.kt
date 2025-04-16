package com.krishnajeena.persona.section.blogs

data class BlogCategoryResponse(
    val categories: List<BlogCategory>
)

data class BlogCategory(
    val name: String,
    val blogs: List<Blog>
)

data class Blog(
    val title: String,
    val url: String
)
