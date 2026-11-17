package com.poswal.noor.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.poswal.noor.data.local.entity.ContentItemEntity
import com.poswal.noor.data.preferences.UserPreferencesRepository
import com.poswal.noor.domain.model.AppLanguage
import com.poswal.noor.domain.model.ContentType
import com.poswal.noor.domain.repository.ContentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val contentRepository: ContentRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _selectedType = MutableStateFlow(ContentType.BOTH)
    val selectedType: StateFlow<ContentType> = _selectedType.asStateFlow()

    val language: StateFlow<AppLanguage> = userPreferencesRepository.userPreferencesFlow
        .map { it.language }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppLanguage.ENGLISH
        )

    val searchResults: StateFlow<List<ContentItemEntity>> = _query
        .debounce(250)
        .distinctUntilChanged()
        .flatMapLatest { q ->
            if (q.trim().length < 2) {
                flowOf(emptyList())
            } else {
                contentRepository.searchContent(q.trim())
            }
        }
        .combine(_selectedType) { results, type ->
            when (type) {
                ContentType.BOTH -> results
                ContentType.QURAN -> results.filter { it.type == "quran" }
                ContentType.HADITH -> results.filter { it.type == "hadith" }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onQueryChanged(newQuery: String) {
        _query.value = newQuery
    }

    fun selectType(type: ContentType) {
        _selectedType.value = type
    }

    fun isBookmarked(contentId: Long): Flow<Boolean> = contentRepository.isBookmarked(contentId)

    fun toggleBookmark(contentId: Long) {
        viewModelScope.launch {
            contentRepository.toggleBookmark(contentId)
        }
    }
}
