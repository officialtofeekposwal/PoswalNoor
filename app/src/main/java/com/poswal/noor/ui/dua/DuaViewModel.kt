package com.poswal.noor.ui.dua

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.poswal.noor.data.local.entity.ContentItemEntity
import com.poswal.noor.data.preferences.UserPreferencesRepository
import com.poswal.noor.domain.model.AppLanguage
import com.poswal.noor.domain.repository.ContentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class DuaCategory(val title: String, val titleAr: String) {
    ALL("All Duas", "جميع الأدعية"),
    MORNING_EVENING("Morning & Evening", "الصباح والمساء"),
    AFTER_PRAYER("After Prayer", "بعد الصلاة"),
    FORGIVENESS("Forgiveness", "الاستغفار والتوبة"),
    HARDSHIP("Hardship & Patience", "الكرب والصبر")
}

@HiltViewModel
class DuaViewModel @Inject constructor(
    private val contentRepository: ContentRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _selectedCategory = MutableStateFlow(DuaCategory.ALL)
    val selectedCategory: StateFlow<DuaCategory> = _selectedCategory.asStateFlow()

    val language: StateFlow<AppLanguage> = userPreferencesRepository.userPreferencesFlow
        .map { it.language }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppLanguage.ENGLISH
        )

    private val allDuasFlow = contentRepository.getFilteredContent(
        types = listOf("quran", "hadith"),
        topics = listOf("dua", "sabr")
    )

    val duas: StateFlow<List<ContentItemEntity>> = combine(allDuasFlow, _selectedCategory) { list, cat ->
        when (cat) {
            DuaCategory.ALL -> list
            DuaCategory.MORNING_EVENING -> list.filter {
                it.translationEn.contains("morning", ignoreCase = true) ||
                it.translationEn.contains("evening", ignoreCase = true) ||
                it.arabic.contains("أصبحنا") || it.arabic.contains("أمسينا")
            }.ifEmpty { list.take(15) }
            DuaCategory.AFTER_PRAYER -> list.filter {
                it.translationEn.contains("prayer", ignoreCase = true) ||
                it.translationEn.contains("salah", ignoreCase = true) ||
                it.arabic.contains("صلاة")
            }.ifEmpty { list.take(15) }
            DuaCategory.FORGIVENESS -> list.filter {
                it.translationEn.contains("forgive", ignoreCase = true) ||
                it.translationEn.contains("repent", ignoreCase = true) ||
                it.arabic.contains("غفر") || it.arabic.contains("توب")
            }.ifEmpty { list.take(20) }
            DuaCategory.HARDSHIP -> list.filter {
                it.topic == "sabr" ||
                it.translationEn.contains("patience", ignoreCase = true) ||
                it.translationEn.contains("difficulty", ignoreCase = true)
            }.ifEmpty { list.take(20) }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun selectCategory(category: DuaCategory) {
        _selectedCategory.value = category
    }

    fun isBookmarked(contentId: Long): Flow<Boolean> = contentRepository.isBookmarked(contentId)

    fun toggleBookmark(contentId: Long) {
        viewModelScope.launch {
            contentRepository.toggleBookmark(contentId, folder = "Duas")
        }
    }
}
