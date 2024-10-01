package com.krishnajeena.persona

import android.app.Application
import androidx.room.Room
import com.krishnajeena.persona.data_layer.BlogUrlDatabase
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BaseClass : Application() {

//    lateinit var blogUrlDatabase : BlogUrlDatabase
//
//    override fun onCreate(){
//        super.onCreate()
//
//        blogUrlDatabase = Room.databaseBuilder(
//            this,
//            BlogUrlDatabase::class.java,
//            "blog_url_database"
//        ).build()
//    }

}