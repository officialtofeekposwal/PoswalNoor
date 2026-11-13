package com.poswal.noor.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.poswal.noor.data.local.entity.ContentItemEntity
import com.poswal.noor.data.preferences.UserPreferencesRepository
import com.poswal.noor.domain.model.AppLanguage
import com.poswal.noor.domain.model.ContentType
import com.poswal.noor.domain.model.Topic
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

data class HomeUiState(
    val currentItem: ContentItemEntity? = null,
    val language: AppLanguage = AppLanguage.ENGLISH,
    val selectedContentType: ContentType = ContentType.BOTH,
    val selectedTopicKey: String? = null, // null = all topics
    val isLoading: Boolean = false
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val contentRepository: ContentRepository,
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _selectedTopicKey = MutableStateFlow<String?>(null)
    private val _selectedContentType = MutableStateFlow(ContentType.BOTH)
    private val _currentItem = MutableStateFlow<ContentItemEntity?>(null)
    private val _isLoading = MutableStateFlow(false)

    val uiState: StateFlow<HomeUiState> = combine(
        preferencesRepository.userPreferencesFlow,
        _selectedTopicKey,
        _selectedContentType,
        _currentItem,
        _isLoading
    ) { prefs, topic, cType, item, loading ->
        HomeUiState(
            currentItem = item,
            language = prefs.language,
            selectedContentType = cType,
            selectedTopicKey = topic,
            isLoading = loading
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState()
    )

    init {
        viewModelScope.launch {
            val prefs = preferencesRepository.getPreferencesSync()
            _selectedContentType.value = prefs.contentType
            refreshRandomItem()
        }
    }

    fun selectTopic(topicKey: String?) {
        _selectedTopicKey.value = topicKey
        refreshRandomItem()
    }

    fun selectContentType(contentType: ContentType) {
        _selectedContentType.value = contentType
        refreshRandomItem()
    }

    fun refreshRandomItem() {
        viewModelScope.launch {
            _isLoading.value = true
            val types = when (_selectedContentType.value) {
                ContentType.BOTH -> listOf("quran", "hadith")
                ContentType.QURAN -> listOf("quran")
                ContentType.HADITH -> listOf("hadith")
            }
            val topics = _selectedTopicKey.value?.let { listOf(it) } ?: emptyList()
            val item = contentRepository.getRandomWidgetContent(
                types = types,
                topics = topics,
                maxWordCount = 500 // All items in home feed
            )
            _currentItem.value = item
            _isLoading.value = false
        }
    }

    fun toggleFavorite(item: ContentItemEntity) {
        viewModelScope.launch {
            val newFavState = !item.isFavorite
            contentRepository.toggleFavorite(item.id, newFavState)
            _currentItem.value = item.copy(isFavorite = newFavState)
        }
    }
}
