package com.poswal.noor

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.poswal.noor.worker.DailyVerseNotificationWorker
import com.poswal.noor.worker.WidgetUpdateWorker
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class NoorApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        try {
            DailyVerseNotificationWorker.schedule(this)
            WidgetUpdateWorker.schedule(this, 6)
        } catch (e: Exception) {
            // Guard in case WorkManager initialization is deferred
        }
    }
}
