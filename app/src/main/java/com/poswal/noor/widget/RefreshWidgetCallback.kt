package com.poswal.noor.widget

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import com.poswal.noor.data.preferences.UserPreferencesRepository
import com.poswal.noor.domain.model.ContentType
import com.poswal.noor.domain.repository.ContentRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface WidgetEntryPoint {
    fun contentRepository(): ContentRepository
    fun userPreferencesRepository(): UserPreferencesRepository
}

class RefreshWidgetCallback : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            WidgetEntryPoint::class.java
        )
        val prefs = entryPoint.userPreferencesRepository().getPreferencesSync()
        val types = when (prefs.contentType) {
            ContentType.BOTH -> listOf("quran", "hadith")
            ContentType.QURAN -> listOf("quran")
            ContentType.HADITH -> listOf("hadith")
        }
        val item = entryPoint.contentRepository().getRandomWidgetContent(
            types = types,
            topics = prefs.selectedTopics.toList(),
            maxWordCount = 40 // Strict word count filter for widget
        )
        if (item != null) {
            NoorGlanceWidget.updateItem(context, glanceId, item.id)
        }
    }
}
