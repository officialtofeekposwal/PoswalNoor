package com.poswal.noor.ui.settings

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.poswal.noor.data.preferences.UserPreferences
import com.poswal.noor.data.preferences.UserPreferencesRepository
import com.poswal.noor.domain.model.AppLanguage
import com.poswal.noor.domain.model.ContentType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val preferences: StateFlow<UserPreferences?> = preferencesRepository.userPreferencesFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    fun setLanguage(language: AppLanguage) {
        viewModelScope.launch {
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(language.tag))
            preferencesRepository.setLanguage(language)
        }
    }

    fun setContentType(contentType: ContentType) {
        viewModelScope.launch {
            preferencesRepository.setContentType(contentType)
        }
    }

    fun toggleTopic(topicKey: String, isSelected: Boolean) {
        val current = preferences.value?.selectedTopics?.toMutableSet() ?: mutableSetOf()
        if (isSelected) {
            current.add(topicKey)
        } else {
            // Keep at least one topic
            if (current.size > 1) {
                current.remove(topicKey)
            }
        }
        viewModelScope.launch {
            preferencesRepository.setSelectedTopics(current)
        }
    }

    fun setAutoRefreshHours(hours: Int) {
        viewModelScope.launch {
            preferencesRepository.setAutoRefreshHours(hours)
        }
    }

    fun setFontSizeScale(scale: Float) {
        viewModelScope.launch {
            preferencesRepository.setFontSizeScale(scale)
        }
    }
}
