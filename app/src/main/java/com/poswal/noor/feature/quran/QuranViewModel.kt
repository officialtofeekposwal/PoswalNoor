package com.poswal.noor.feature.quran

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.poswal.noor.data.local.entity.SurahEntity
import com.poswal.noor.data.preferences.UserPreferencesRepository
import com.poswal.noor.domain.repository.ContentRepository
import com.poswal.noor.feature.quran.model.JuzItem
import com.poswal.noor.feature.quran.model.QuranJuzData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class QuranTab {
    SURAH,
    JUZ
}

data class QuranUiState(
    val selectedTab: QuranTab = QuranTab.SURAH,
    val surahs: List<SurahEntity> = emptyList(),
    val filteredSurahs: List<SurahEntity> = emptyList(),
    val juzList: List<JuzItem> = QuranJuzData.ALL_JUZ,
    val filteredJuzList: List<JuzItem> = QuranJuzData.ALL_JUZ,
    val searchQuery: String = "",
    val isSearchActive: Boolean = false,
    val lastReadSurah: Int = 1,
    val lastReadAyah: Int = 1,
    val lastReadSurahName: String = "Al-Fatihah",
    val isLoading: Boolean = true
)

@HiltViewModel
class QuranViewModel @Inject constructor(
    private val contentRepository: ContentRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuranUiState())
    val uiState: StateFlow<QuranUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")

    init {
        loadSurahs()
        observeLastRead()
    }

    private fun loadSurahs() {
        viewModelScope.launch {
            combine(
                contentRepository.getAllSurahs(),
                _searchQuery
            ) { surahs, query ->
                val filtered = if (query.isBlank()) {
                    surahs
                } else {
                    surahs.filter { surah ->
                        surah.nameEnglish.contains(query, ignoreCase = true) ||
                                surah.nameArabic.contains(query, ignoreCase = true) ||
                                surah.nameTranslation.contains(query, ignoreCase = true) ||
                                surah.number.toString() == query.trim()
                    }
                }

                val filteredJuz = if (query.isBlank()) {
                    QuranJuzData.ALL_JUZ
                } else {
                    QuranJuzData.ALL_JUZ.filter { juz ->
                        juz.nameEnglish.contains(query, ignoreCase = true) ||
                                juz.nameArabic.contains(query, ignoreCase = true) ||
                                juz.number.toString() == query.trim() ||
                                juz.startSurahName.contains(query, ignoreCase = true)
                    }
                }

                _uiState.update {
                    it.copy(
                        surahs = surahs,
                        filteredSurahs = filtered,
                        filteredJuzList = filteredJuz,
                        isLoading = surahs.isEmpty(),
                        searchQuery = query
                    )
                }
            }.collect {}
        }
    }

    private fun observeLastRead() {
        viewModelScope.launch {
            userPreferencesRepository.lastReadFlow.collect { (surah, ayah, name) ->
                _uiState.update {
                    it.copy(
                        lastReadSurah = surah,
                        lastReadAyah = ayah,
                        lastReadSurahName = name
                    )
                }
            }
        }
    }

    fun selectTab(tab: QuranTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun toggleSearch(active: Boolean) {
        _uiState.update { it.copy(isSearchActive = active) }
        if (!active) {
            _searchQuery.value = ""
        }
    }
}
