package com.poswal.noor.widget

import android.content.Context
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.ActionParameters
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.currentState
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
import androidx.glance.text.TextStyle

class IncrementTasbihCallback : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        updateAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId) { prefs ->
            val key = intPreferencesKey("widget_tasbih_count")
            val current = prefs[key] ?: 0
            val next = if (current >= 33) 1 else current + 1
            prefs.toMutablePreferences().apply {
                this[key] = next
            }
        }
        TasbihGlanceWidget().update(context, glanceId)
    }
}

class TasbihGlanceWidget : GlanceAppWidget() {

    override val stateDefinition = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                val state = currentState<Preferences>()
                val count = state[intPreferencesKey("widget_tasbih_count")] ?: 0

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

                Box(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .cornerRadius(24.dp)
                        .background(deepEmerald)
                        .padding(14.dp)
                        .clickable(actionRunCallback<IncrementTasbihCallback>()),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = GlanceModifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "سُبْحَانَ اللَّهِ",
                            style = TextStyle(color = goldColor, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = GlanceModifier.height(4.dp))
                        Text(
                            text = "$count",
                            style = TextStyle(color = whiteColor, fontSize = 36.sp, fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "/ 33",
                            style = TextStyle(color = goldColor, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                        )
                        Spacer(modifier = GlanceModifier.height(4.dp))
                        Text(
                            text = "Tap to count",
                            style = TextStyle(color = whiteColor, fontSize = 10.sp)
                        )
                    }
                }
            }
        }
    }
}
