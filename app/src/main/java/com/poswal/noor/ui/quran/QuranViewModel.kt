package com.poswal.noor.ui.quran

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.poswal.noor.data.local.entity.SurahEntity
import com.poswal.noor.data.preferences.UserPreferencesRepository
import com.poswal.noor.domain.repository.ContentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuranViewModel @Inject constructor(
    private val contentRepository: ContentRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val allSurahs = contentRepository.getAllSurahs()

    val lastRead = userPreferencesRepository.lastReadFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = Triple(1, 1, "Al-Fatihah")
    )

    val surahs: StateFlow<List<SurahEntity>> = combine(allSurahs, _searchQuery) { list, query ->
        if (query.isBlank()) {
            list
        } else {
            val q = query.trim().lowercase()
            list.filter {
                it.number.toString().contains(q) ||
                it.nameEnglish.lowercase().contains(q) ||
                it.nameTranslation.lowercase().contains(q) ||
                it.nameArabic.contains(q)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun saveLastRead(surah: Int, ayah: Int, surahName: String) {
        viewModelScope.launch {
            userPreferencesRepository.saveLastRead(surah, ayah, surahName)
        }
    }
}
