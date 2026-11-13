package com.poswal.noor.worker

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.poswal.noor.data.preferences.UserPreferencesRepository
import com.poswal.noor.domain.model.ContentType
import com.poswal.noor.domain.repository.ContentRepository
import com.poswal.noor.widget.NoorGlanceWidget
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

@HiltWorker
class WidgetUpdateWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val contentRepository: ContentRepository,
    private val preferencesRepository: UserPreferencesRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val prefs = preferencesRepository.getPreferencesSync()
            val types = when (prefs.contentType) {
                ContentType.BOTH -> listOf("quran", "hadith")
                ContentType.QURAN -> listOf("quran")
                ContentType.HADITH -> listOf("hadith")
            }
            val item = contentRepository.getRandomWidgetContent(
                types = types,
                topics = prefs.selectedTopics.toList(),
                maxWordCount = 40
            )

            if (item != null) {
                val glanceManager = GlanceAppWidgetManager(appContext)
                val glanceIds = glanceManager.getGlanceIds(NoorGlanceWidget::class.java)
                glanceIds.forEach { glanceId ->
                    NoorGlanceWidget.updateItem(appContext, glanceId, item.id)
                }
            }
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        private const val WORK_NAME = "poswal_noor_widget_periodic_update"

        fun schedule(context: Context, repeatIntervalHours: Int) {
            val workRequest = PeriodicWorkRequestBuilder<WidgetUpdateWorker>(
                repeatIntervalHours.toLong(),
                TimeUnit.HOURS
            ).build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                workRequest
            )
        }
    }
}
