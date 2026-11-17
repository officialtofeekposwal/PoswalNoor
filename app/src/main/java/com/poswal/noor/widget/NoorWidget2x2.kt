package com.poswal.noor.widget

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import com.poswal.noor.domain.model.translationFor
import com.poswal.noor.ui.MainActivity
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.runBlocking

class NoorWidget2x2 : GlanceAppWidget() {

    override val stateDefinition = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            WidgetEntryPoint::class.java
        )
        val prefs = entryPoint.userPreferencesRepository().getPreferencesSync()
        val contentRepo = entryPoint.contentRepository()

        val item = runBlocking {
            val epochDay = System.currentTimeMillis() / (1000 * 60 * 60 * 24)
            contentRepo.getVerseOfTheDay(epochDay) ?: contentRepo.getNoRepeatItemForWidget(
                types = listOf("quran", "hadith"),
                topics = emptyList(),
                maxWordCount = 30
            )
        }

        provideContent {
            GlanceTheme {
                val deepEmerald = ColorProvider(
                    day = androidx.compose.ui.graphics.Color(0xFF064E3B),
                    night = androidx.compose.ui.graphics.Color(0xFF022C22)
                )
                val goldColor = ColorProvider(
                    day = androidx.compose.ui.graphics.Color(0xFFF59E0B),
                    night = androidx.compose.ui.graphics.Color(0xFFFBBF24)
                )
                val whiteColor = ColorProvider(
                    day = androidx.compose.ui.graphics.Color.White,
                    night = androidx.compose.ui.graphics.Color(0xFFF1F5F9)
                )

                val contentIdParamKey = androidx.glance.action.ActionParameters.Key<Long>("contentId")
                val clickAction = if (item != null) {
                    androidx.glance.action.actionStartActivity<MainActivity>(
                        androidx.glance.action.actionParametersOf(contentIdParamKey to item.id)
                    )
                } else {
                    androidx.glance.action.actionStartActivity<MainActivity>()
                }

                Box(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .cornerRadius(24.dp)
                        .background(deepEmerald)
                        .padding(14.dp)
                        .clickable(clickAction),
                    contentAlignment = Alignment.Center
                ) {
                    if (item == null) {
                        Text(
                            text = "Poswal Noor",
                            style = TextStyle(color = whiteColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        )
                    } else {
                        Column(
                            modifier = GlanceModifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "VERSE OF THE DAY",
                                style = TextStyle(
                                    color = goldColor,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )

                            Spacer(modifier = GlanceModifier.height(6.dp))

                            val text = item.translationFor(prefs.language) ?: item.translationEn
                            Text(
                                text = text,
                                style = TextStyle(
                                    color = whiteColor,
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center
                                ),
                                maxLines = 4
                            )

                            Spacer(modifier = GlanceModifier.height(6.dp))

                            Text(
                                text = item.reference,
                                style = TextStyle(
                                    color = goldColor,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
