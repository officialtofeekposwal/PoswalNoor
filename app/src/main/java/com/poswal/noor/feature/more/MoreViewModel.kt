package com.poswal.noor.feature.more

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.poswal.noor.data.preferences.NoorThemeMode
import com.poswal.noor.data.preferences.UserPreferences
import com.poswal.noor.data.preferences.UserPreferencesRepository
import com.poswal.noor.domain.model.AppLanguage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MoreViewModel @Inject constructor(
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val userPreferences: StateFlow<UserPreferences?> = preferencesRepository.userPreferencesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun setLanguage(language: AppLanguage) {
        viewModelScope.launch {
            preferencesRepository.setLanguage(language)
        }
    }

    fun setThemeMode(themeMode: NoorThemeMode) {
        viewModelScope.launch {
            preferencesRepository.setThemeMode(themeMode)
        }
    }

    fun setFontSizeScale(scale: Float) {
        viewModelScope.launch {
            preferencesRepository.setFontSizeScale(scale)
        }
    }

    fun resetTasbihLifetime() {
        viewModelScope.launch {
            preferencesRepository.resetTasbihLifetime()
        }
    }
}
