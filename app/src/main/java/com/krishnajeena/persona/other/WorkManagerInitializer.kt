package com.krishnajeena.persona.other

import android.content.Context
import androidx.startup.Initializer
import androidx.work.Configuration
import androidx.work.WorkManager
import dagger.hilt.android.EntryPointAccessors

class WorkManagerInitializer : Initializer<WorkManager> {
    override fun create(context: Context): WorkManager {
        val entryPoint = EntryPointAccessors.fromApplication(
            context,
            WorkManagerInitializerEntryPoint::class.java
        )
        val config = Configuration.Builder()
            .setWorkerFactory(entryPoint.hiltWorkerFactory())
            .build()
        WorkManager.initialize(context, config)
        return WorkManager.getInstance(context)
    }
    override fun dependencies() = emptyList<Class<out Initializer<*>>>()
}
