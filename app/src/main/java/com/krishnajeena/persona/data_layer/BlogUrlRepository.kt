package com.krishnajeena.persona.data_layer

class BlogUrlRepository(private val blogUrlDao: BlogUrlDao) {
    suspend fun insertUrl(url: BlogUrl) {
        blogUrlDao.insertUrl(url)
    }
    suspend fun getAllUrls(): List<BlogUrl> {
        return blogUrlDao.getAllUrls()
    }
    suspend fun deleteUrl(url: BlogUrl) {
        blogUrlDao.deleteUrl(url)
    }
}