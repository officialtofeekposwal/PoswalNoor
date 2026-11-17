package com.poswal.noor.feature.quran

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.poswal.noor.data.local.entity.ContentItemEntity
import com.poswal.noor.data.local.entity.SurahEntity
import com.poswal.noor.data.preferences.UserPreferencesRepository
import com.poswal.noor.domain.model.AppLanguage
import com.poswal.noor.domain.repository.ContentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class QuranReaderUiState(
    val surahNumber: Int = 1,
    val initialAyahNumber: Int = 1,
    val surah: SurahEntity? = null,
    val ayahs: List<ContentItemEntity> = emptyList(),
    val language: AppLanguage = AppLanguage.ENGLISH,
    val bookmarkedAyahIds: Set<Long> = emptySet(),
    val arabicFontSize: Float = 28f,
    val translationFontSize: Float = 16f,
    val showTranslation: Boolean = true,
    val lineSpacingMultiplier: Float = 2.0f,
    val currentAyah: Int = 1,
    val isLoading: Boolean = true
)

@HiltViewModel
class QuranReaderViewModel @Inject constructor(
    private val contentRepository: ContentRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val initialSurahNumber: Int = savedStateHandle.get<Int>("surahNumber") ?: 1
    private val initialAyahNumber: Int = savedStateHandle.get<Int>("ayahNumber") ?: 1

    private val _uiState = MutableStateFlow(
        QuranReaderUiState(
            surahNumber = initialSurahNumber,
            initialAyahNumber = initialAyahNumber,
            currentAyah = initialAyahNumber
        )
    )
    val uiState: StateFlow<QuranReaderUiState> = _uiState.asStateFlow()

    init {
        loadSurahData(initialSurahNumber)
        observePreferences()
        observeBookmarks()
    }

    fun loadSurah(surahNumber: Int, startAyah: Int = 1) {
        _uiState.update {
            it.copy(
                surahNumber = surahNumber,
                initialAyahNumber = startAyah,
                currentAyah = startAyah,
                isLoading = true
            )
        }
        loadSurahData(surahNumber)
    }

    private fun loadSurahData(surahNumber: Int) {
        viewModelScope.launch {
            val surah = contentRepository.getSurahByNumber(surahNumber)
            _uiState.update { it.copy(surah = surah) }

            contentRepository.getAyahsForSurah(surahNumber).collect { ayahs ->
                _uiState.update {
                    it.copy(
                        ayahs = ayahs,
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun observePreferences() {
        viewModelScope.launch {
            userPreferencesRepository.userPreferencesFlow.collect { prefs ->
                _uiState.update {
                    it.copy(
                        language = prefs.language,
                        arabicFontSize = 28f * prefs.fontSizeScale,
                        translationFontSize = 16f * prefs.fontSizeScale
                    )
                }
            }
        }
    }

    private fun observeBookmarks() {
        viewModelScope.launch {
            contentRepository.getAllBookmarks()
                .map { list -> list.map { it.contentId }.toSet() }
                .collect { ids ->
                    _uiState.update { it.copy(bookmarkedAyahIds = ids) }
                }
        }
    }

    private var saveLastReadJob: Job? = null

    fun onAyahScrolledTo(ayahNumber: Int) {
        val surah = _uiState.value.surah ?: return
        if (ayahNumber == _uiState.value.currentAyah) return

        _uiState.update { it.copy(currentAyah = ayahNumber) }
        saveLastReadJob?.cancel()
        saveLastReadJob = viewModelScope.launch {
            delay(500L) // Debounce rapid scroll events through long surahs
            userPreferencesRepository.saveLastRead(
                surah = surah.number,
                ayah = ayahNumber,
                surahName = surah.nameEnglish
            )
        }
    }

    fun toggleBookmark(contentId: Long) {
        viewModelScope.launch {
            contentRepository.toggleBookmark(contentId)
        }
    }

    fun saveNote(contentId: Long, note: String) {
        viewModelScope.launch {
            contentRepository.updateBookmarkNote(contentId, note)
        }
    }

    fun updateArabicFontSize(size: Float) {
        _uiState.update { it.copy(arabicFontSize = size) }
        viewModelScope.launch {
            userPreferencesRepository.setFontSizeScale(size / 28f)
        }
    }

    fun updateTranslationFontSize(size: Float) {
        _uiState.update { it.copy(translationFontSize = size) }
    }

    fun toggleShowTranslation(show: Boolean) {
        _uiState.update { it.copy(showTranslation = show) }
    }

    fun updateLineSpacingMultiplier(multiplier: Float) {
        _uiState.update { it.copy(lineSpacingMultiplier = multiplier) }
    }
}
