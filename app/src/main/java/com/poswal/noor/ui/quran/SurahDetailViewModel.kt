package com.poswal.noor.ui.quran

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.poswal.noor.data.local.entity.ContentItemEntity
import com.poswal.noor.data.local.entity.SurahEntity
import com.poswal.noor.data.preferences.UserPreferencesRepository
import com.poswal.noor.domain.model.AppLanguage
import com.poswal.noor.domain.repository.ContentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SurahDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val contentRepository: ContentRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val surahNumber: Int = checkNotNull(savedStateHandle["surahNumber"]) {
        "surahNumber argument is required"
    }.toString().toInt()

    private val _surah = MutableStateFlow<SurahEntity?>(null)
    val surah: StateFlow<SurahEntity?> = _surah.asStateFlow()

    val ayahs: StateFlow<List<ContentItemEntity>> = contentRepository.getAyahsForSurah(surahNumber)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val language: StateFlow<AppLanguage> = userPreferencesRepository.userPreferencesFlow
        .map { it.language }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppLanguage.ENGLISH
        )

    init {
        viewModelScope.launch {
            _surah.value = contentRepository.getSurahByNumber(surahNumber)
        }
    }

    fun isBookmarked(contentId: Long): Flow<Boolean> = contentRepository.isBookmarked(contentId)

    fun toggleBookmark(contentId: Long) {
        viewModelScope.launch {
            contentRepository.toggleBookmark(contentId, folder = "Quran")
        }
    }

    fun onAyahVisible(ayahNumber: Int, surahName: String) {
        viewModelScope.launch {
            userPreferencesRepository.saveLastRead(surahNumber, ayahNumber, surahName)
        }
    }
}
