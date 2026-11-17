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

enum class NoorThemeMode(val title: String) {
    SYSTEM("System Default"),
    LIGHT("Light (Ivory)"),
    DARK("Dark (OLED Calm)")
}

data class UserPreferences(
    val language: AppLanguage,
    val contentType: ContentType,
    val selectedTopics: Set<String>,
    val autoRefreshHours: Int,
    val fontSizeScale: Float,
    val isOnboardingCompleted: Boolean,
    val themeMode: NoorThemeMode = NoorThemeMode.SYSTEM,
    val tasbihLifetime: Int = 0
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
        val KEY_LAST_READ_SURAH = intPreferencesKey("last_read_surah")
        val KEY_LAST_READ_AYAH = intPreferencesKey("last_read_ayah")
        val KEY_LAST_READ_SURAH_NAME = stringPreferencesKey("last_read_surah_name")
        val KEY_THEME_MODE = stringPreferencesKey("theme_mode")
        val KEY_TASBIH_LIFETIME = intPreferencesKey("tasbih_lifetime")
    }

    val lastReadFlow: Flow<Triple<Int, Int, String>> = context.dataStore.data.map { prefs ->
        Triple(
            prefs[KEY_LAST_READ_SURAH] ?: 1,
            prefs[KEY_LAST_READ_AYAH] ?: 1,
            prefs[KEY_LAST_READ_SURAH_NAME] ?: "Al-Fatihah"
        )
    }

    suspend fun saveLastRead(surah: Int, ayah: Int, surahName: String) {
        context.dataStore.edit {
            it[KEY_LAST_READ_SURAH] = surah
            it[KEY_LAST_READ_AYAH] = ayah
            it[KEY_LAST_READ_SURAH_NAME] = surahName
        }
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
        val themeModeStr = prefs[KEY_THEME_MODE] ?: NoorThemeMode.SYSTEM.name
        val themeMode = try { NoorThemeMode.valueOf(themeModeStr) } catch (e: Exception) { NoorThemeMode.SYSTEM }
        val tasbihLifetime = prefs[KEY_TASBIH_LIFETIME] ?: 0

        UserPreferences(
            language = lang,
            contentType = cType,
            selectedTopics = topics,
            autoRefreshHours = refreshHours,
            fontSizeScale = fontScale,
            isOnboardingCompleted = onboarding,
            themeMode = themeMode,
            tasbihLifetime = tasbihLifetime
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

    suspend fun setThemeMode(themeMode: NoorThemeMode) {
        context.dataStore.edit { it[KEY_THEME_MODE] = themeMode.name }
    }

    suspend fun incrementTasbihLifetime(amount: Int = 1) {
        context.dataStore.edit {
            val current = it[KEY_TASBIH_LIFETIME] ?: 0
            it[KEY_TASBIH_LIFETIME] = current + amount
        }
    }

    suspend fun resetTasbihLifetime() {
        context.dataStore.edit { it[KEY_TASBIH_LIFETIME] = 0 }
    }
}
