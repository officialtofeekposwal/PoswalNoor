package com.poswal.noor.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.poswal.noor.data.local.entity.ContentItemEntity
import com.poswal.noor.data.preferences.UserPreferencesRepository
import com.poswal.noor.domain.model.AppLanguage
import com.poswal.noor.domain.model.ContentType
import com.poswal.noor.domain.repository.ContentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val language: AppLanguage = AppLanguage.ENGLISH,
    val selectedContentType: ContentType = ContentType.BOTH,
    val selectedTopicKey: String? = null,
    val isRefreshing: Boolean = false
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val contentRepository: ContentRepository,
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _selectedTopicKey = MutableStateFlow<String?>(null)
    private val _selectedContentType = MutableStateFlow(ContentType.BOTH)
    private val _sessionSeed = MutableStateFlow(System.currentTimeMillis())
    private val _isRefreshing = MutableStateFlow(false)

    val uiState: StateFlow<HomeUiState> = combine(
        preferencesRepository.userPreferencesFlow,
        _selectedTopicKey,
        _selectedContentType,
        _isRefreshing
    ) { prefs, topic, cType, refreshing ->
        HomeUiState(
            language = prefs.language,
            selectedContentType = cType,
            selectedTopicKey = topic,
            isRefreshing = refreshing
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState()
    )

    // Paging 3 Flow reacting to filter changes and session seed
    val pagedFeed: Flow<PagingData<ContentItemEntity>> = combine(
        _selectedContentType,
        _selectedTopicKey,
        _sessionSeed
    ) { cType, topic, seed ->
        Triple(cType, topic, seed)
    }.flatMapLatest { (cType, topic, seed) ->
        contentRepository.getPagedFeed(cType, topic, seed)
    }.cachedIn(viewModelScope)

    init {
        viewModelScope.launch {
            val prefs = preferencesRepository.getPreferencesSync()
            _selectedContentType.value = prefs.contentType
        }
    }

    fun selectTopic(topicKey: String?) {
        _selectedTopicKey.value = topicKey
    }

    fun selectContentType(contentType: ContentType) {
        _selectedContentType.value = contentType
    }

    fun refreshFeed() {
        viewModelScope.launch {
            _isRefreshing.value = true
            _sessionSeed.value = System.currentTimeMillis()
            _isRefreshing.value = false
        }
    }

    fun onItemVisible(item: ContentItemEntity) {
        viewModelScope.launch {
            contentRepository.markItemSeen(item.id, item.type)
        }
    }

    fun toggleFavorite(item: ContentItemEntity) {
        viewModelScope.launch {
            contentRepository.toggleBookmark(item.id)
        }
    }

    fun isBookmarked(contentId: Long): Flow<Boolean> {
        return contentRepository.isBookmarked(contentId)
    }
}
