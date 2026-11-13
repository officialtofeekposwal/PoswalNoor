package com.poswal.noor.widget

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.actionParametersOf
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.glance.appwidget.cornerRadius
import com.poswal.noor.R
import com.poswal.noor.domain.model.AppLanguage
import com.poswal.noor.domain.model.ContentType
import com.poswal.noor.domain.model.translationFor
import com.poswal.noor.ui.MainActivity
import dagger.hilt.android.EntryPointAccessors
import java.util.Locale

class NoorGlanceWidget : GlanceAppWidget() {

    override val stateDefinition = PreferencesGlanceStateDefinition

    companion object {
        val KEY_CONTENT_ID = longPreferencesKey("widget_content_id")

        suspend fun updateItem(context: Context, glanceId: GlanceId, contentId: Long) {
            updateAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId) { prefs ->
                prefs.toMutablePreferences().apply {
                    this[KEY_CONTENT_ID] = contentId
                }
            }
            NoorGlanceWidget().update(context, glanceId)
        }
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            WidgetEntryPoint::class.java
        )
        val prefs = entryPoint.userPreferencesRepository().getPreferencesSync()
        val lang = prefs.language

        // Localized context for strings and direction
        val localizedContext = context.createConfigurationContext(
            Configuration(context.resources.configuration).apply {
                setLocale(Locale(lang.tag))
            }
        )

        provideContent {
            GlanceTheme {
                val state = currentState<Preferences>()
                val savedId = state[KEY_CONTENT_ID]

                // Content fetch
                val types = when (prefs.contentType) {
                    ContentType.BOTH -> listOf("quran", "hadith")
                    ContentType.QURAN -> listOf("quran")
                    ContentType.HADITH -> listOf("hadith")
                }
                val contentRepo = entryPoint.contentRepository()
                val item = kotlinx.coroutines.runBlocking {
                    if (savedId != null && savedId > 0) {
                        contentRepo.getContentByIdSync(savedId)
                    } else {
                        contentRepo.getRandomWidgetContent(
                            types = types,
                            topics = prefs.selectedTopics.toList(),
                            maxWordCount = 40
                        )
                    }
                }

                Box(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .background(GlanceTheme.colors.background)
                        .cornerRadius(16.dp)
                        .padding(14.dp)
                ) {
                    if (item == null) {
                        Column(
                            modifier = GlanceModifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Poswal Noor",
                                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            )
                        }
                    } else {
                        Column(
                            modifier = GlanceModifier.fillMaxSize(),
                            verticalAlignment = Alignment.Top
                        ) {
                            // Header: Poswal Noor + Type Tag
                            Row(
                                modifier = GlanceModifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Poswal Noor",
                                    style = TextStyle(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = GlanceTheme.colors.primary
                                    )
                                )
                                Spacer(modifier = GlanceModifier.defaultWeight())
                                Text(
                                    text = item.type.uppercase(),
                                    style = TextStyle(
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 11.sp,
                                        color = GlanceTheme.colors.secondary
                                    )
                                )
                            }

                            Spacer(modifier = GlanceModifier.height(8.dp))

                            // Arabic Text (<= 40 words)
                            Text(
                                text = item.arabic,
                                style = TextStyle(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    textAlign = TextAlign.Center,
                                    color = GlanceTheme.colors.onBackground
                                ),
                                maxLines = 3,
                                modifier = GlanceModifier.fillMaxWidth()
                            )

                            // Translation
                            val translation = item.translationFor(lang)
                            if (!translation.isNullOrEmpty()) {
                                Spacer(modifier = GlanceModifier.height(4.dp))
                                Text(
                                    text = translation,
                                    style = TextStyle(
                                        fontSize = 12.sp,
                                        color = GlanceTheme.colors.onBackground
                                    ),
                                    maxLines = 2,
                                    modifier = GlanceModifier.fillMaxWidth()
                                )
                            }

                            Spacer(modifier = GlanceModifier.height(6.dp))

                            // Footer: Reference in Bold + Actions
                            Row(
                                modifier = GlanceModifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = item.reference,
                                    style = TextStyle(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = GlanceTheme.colors.primary
                                    )
                                )

                                Spacer(modifier = GlanceModifier.defaultWeight())

                                // Refresh action
                                Text(
                                    text = localizedContext.getString(R.string.action_refresh),
                                    style = TextStyle(
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = GlanceTheme.colors.secondary
                                    ),
                                    modifier = GlanceModifier
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                        .clickable(actionRunCallback<RefreshWidgetCallback>())
                                )

                                // Read more action
                                val contentIdParamKey = androidx.glance.action.ActionParameters.Key<Long>("contentId")
                                Text(
                                    text = localizedContext.getString(R.string.action_read_more),
                                    style = TextStyle(
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GlanceTheme.colors.primary
                                    ),
                                    modifier = GlanceModifier
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                        .clickable(
                                            actionStartActivity<MainActivity>(
                                                actionParametersOf(contentIdParamKey to item.id)
                                            )
                                        )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
