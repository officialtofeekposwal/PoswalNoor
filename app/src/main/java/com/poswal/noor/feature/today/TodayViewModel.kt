package com.poswal.noor.feature.today

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.poswal.noor.data.local.entity.ContentItemEntity
import com.poswal.noor.data.preferences.UserPreferencesRepository
import com.poswal.noor.domain.model.AppLanguage
import com.poswal.noor.domain.model.ContentType
import com.poswal.noor.domain.repository.ContentRepository
import com.poswal.noor.util.HijriCalendarHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject
import kotlin.random.Random

data class TodayUiState(
    val heroVerse: ContentItemEntity? = null,
    val isHeroBookmarked: Boolean = false,
    val lastReadSurah: Int = 1,
    val lastReadAyah: Int = 1,
    val lastReadSurahName: String = "Al-Fatihah",
    val totalVersesForSurah: Int = 7,
    val selectedContentType: ContentType = ContentType.BOTH,
    val selectedTopicKey: String? = null,
    val hijriDateFormatted: String = "",
    val language: AppLanguage = AppLanguage.ENGLISH,
    val isRefreshing: Boolean = false,
    val bookmarkedIds: Set<Long> = emptySet()
)

@HiltViewModel
class TodayViewModel @Inject constructor(
    private val contentRepository: ContentRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        TodayUiState(
            hijriDateFormatted = HijriCalendarHelper.getHijriDate().formattedDate
        )
    )
    val uiState: StateFlow<TodayUiState> = _uiState.asStateFlow()

    private val _selectedContentType = MutableStateFlow(ContentType.BOTH)
    private val _selectedTopicKey = MutableStateFlow<String?>(null)
    private val _sessionSeed = MutableStateFlow(System.currentTimeMillis())

    @OptIn(ExperimentalCoroutinesApi::class)
    val pagedFeed: Flow<PagingData<ContentItemEntity>> = combine(
        _selectedContentType,
        _selectedTopicKey,
        _sessionSeed
    ) { type, topic, seed ->
        Triple(type, topic, seed)
    }.distinctUntilChanged()
        .flatMapLatest { (type, topic, seed) ->
            contentRepository.getPagedFeed(type, topic, seed)
        }.cachedIn(viewModelScope)

    init {
        loadHeroVerse()
        observePreferences()
        observeBookmarks()
        observeLastRead()
    }

    private fun loadHeroVerse() {
        viewModelScope.launch {
            val epochDay = try {
                LocalDate.now().toEpochDay()
            } catch (e: Exception) {
                System.currentTimeMillis() / (1000 * 60 * 60 * 24)
            }
            val hero = contentRepository.getVerseOfTheDay(epochDay)
            _uiState.update { it.copy(heroVerse = hero) }

            // Observe hero bookmark state if hero is present
            if (hero != null) {
                contentRepository.isBookmarked(hero.id).collect { isBookmarked ->
                    _uiState.update { it.copy(isHeroBookmarked = isBookmarked) }
                }
            }
        }
    }

    private fun observePreferences() {
        viewModelScope.launch {
            userPreferencesRepository.userPreferencesFlow.collect { prefs ->
                _uiState.update { it.copy(language = prefs.language) }
            }
        }
    }

    private fun observeBookmarks() {
        viewModelScope.launch {
            contentRepository.getAllBookmarks()
                .map { bookmarks -> bookmarks.map { it.contentId }.toSet() }
                .collect { bookmarkedIds ->
                    _uiState.update { state ->
                        state.copy(
                            bookmarkedIds = bookmarkedIds,
                            isHeroBookmarked = state.heroVerse?.id?.let { bookmarkedIds.contains(it) } ?: false
                        )
                    }
                }
        }
    }

    private fun observeLastRead() {
        viewModelScope.launch {
            userPreferencesRepository.lastReadFlow.collect { (surah, ayah, name) ->
                val surahEntity = contentRepository.getSurahByNumber(surah)
                val totalVerses = surahEntity?.totalVerses ?: 7
                _uiState.update {
                    it.copy(
                        lastReadSurah = surah,
                        lastReadAyah = ayah,
                        lastReadSurahName = name,
                        totalVersesForSurah = totalVerses
                    )
                }
            }
        }
    }

    fun onContentTypeSelected(type: ContentType) {
        _selectedContentType.value = type
        _uiState.update { it.copy(selectedContentType = type) }
    }

    fun onTopicSelected(topicKey: String?) {
        _selectedTopicKey.value = topicKey
        _uiState.update { it.copy(selectedTopicKey = topicKey) }
    }

    fun refreshFeed() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            _sessionSeed.value = Random.nextLong(1, Long.MAX_VALUE)
            val epochDay = try {
                LocalDate.now().toEpochDay()
            } catch (e: Exception) {
                System.currentTimeMillis() / (1000 * 60 * 60 * 24)
            }
            val hero = contentRepository.getVerseOfTheDay(epochDay)
            _uiState.update {
                it.copy(
                    heroVerse = hero,
                    isRefreshing = false,
                    hijriDateFormatted = HijriCalendarHelper.getHijriDate().formattedDate
                )
            }
        }
    }

    fun toggleBookmark(contentId: Long) {
        viewModelScope.launch {
            contentRepository.toggleBookmark(contentId)
        }
    }

    fun onItemVisible(item: ContentItemEntity) {
        viewModelScope.launch {
            contentRepository.markItemSeen(item.id, item.type)
        }
    }
}
