package com.poswal.noor.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.poswal.noor.domain.model.AppLanguage
import com.poswal.noor.domain.model.ContentType
import com.poswal.noor.domain.model.Topic
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "poswal_noor_prefs")

data class UserPreferences(
    val language: AppLanguage,
    val contentType: ContentType,
    val selectedTopics: Set<String>,
    val autoRefreshHours: Int,
    val fontSizeScale: Float,
    val isOnboardingCompleted: Boolean
)

@Singleton
class UserPreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        val KEY_APP_LANGUAGE = stringPreferencesKey("app_language")
        val KEY_CONTENT_TYPE = stringPreferencesKey("content_type")
        val KEY_SELECTED_TOPICS = stringSetPreferencesKey("selected_topics")
        val KEY_AUTO_REFRESH_HOURS = intPreferencesKey("auto_refresh_hours")
        val KEY_FONT_SIZE_SCALE = floatPreferencesKey("font_size_scale")
        val KEY_ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
    }

    private fun detectDefaultLanguage(): AppLanguage {
        val systemLang = Locale.getDefault().language
        return AppLanguage.fromTag(systemLang)
    }

    val userPreferencesFlow: Flow<UserPreferences> = context.dataStore.data.map { prefs ->
        val langTag = prefs[KEY_APP_LANGUAGE]
        val lang = if (langTag != null) AppLanguage.fromTag(langTag) else detectDefaultLanguage()
        val cTypeKey = prefs[KEY_CONTENT_TYPE]
        val cType = ContentType.fromKey(cTypeKey)
        val topics = prefs[KEY_SELECTED_TOPICS] ?: Topic.ALL_KEYS
        val refreshHours = prefs[KEY_AUTO_REFRESH_HOURS] ?: 6
        val fontScale = prefs[KEY_FONT_SIZE_SCALE] ?: 1.0f
        val onboarding = prefs[KEY_ONBOARDING_COMPLETED] ?: false

        UserPreferences(
            language = lang,
            contentType = cType,
            selectedTopics = topics,
            autoRefreshHours = refreshHours,
            fontSizeScale = fontScale,
            isOnboardingCompleted = onboarding
        )
    }

    suspend fun getPreferencesSync(): UserPreferences = userPreferencesFlow.first()

    suspend fun setLanguage(language: AppLanguage) {
        context.dataStore.edit { it[KEY_APP_LANGUAGE] = language.tag }
    }

    suspend fun setContentType(contentType: ContentType) {
        context.dataStore.edit { it[KEY_CONTENT_TYPE] = contentType.key }
    }

    suspend fun setSelectedTopics(topics: Set<String>) {
        context.dataStore.edit { it[KEY_SELECTED_TOPICS] = topics }
    }

    suspend fun setAutoRefreshHours(hours: Int) {
        context.dataStore.edit { it[KEY_AUTO_REFRESH_HOURS] = hours }
    }

    suspend fun setFontSizeScale(scale: Float) {
        context.dataStore.edit { it[KEY_FONT_SIZE_SCALE] = scale }
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { it[KEY_ONBOARDING_COMPLETED] = completed }
    }
}
