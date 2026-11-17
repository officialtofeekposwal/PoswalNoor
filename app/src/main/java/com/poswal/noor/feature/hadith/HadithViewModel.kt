package com.poswal.noor.feature.hadith

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.poswal.noor.data.local.entity.ContentItemEntity
import com.poswal.noor.domain.model.HadithBookInfo
import com.poswal.noor.domain.repository.ContentRepository
import com.poswal.noor.feature.hadith.model.HadithCollectionInfo
import com.poswal.noor.feature.hadith.model.HadithCollectionsData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HadithUiState(
    val collections: List<HadithCollectionInfo> = HadithCollectionsData.COLLECTIONS,
    val selectedCollection: HadithCollectionInfo? = null,
    val selectedBook: HadithBookInfo? = null,
    val books: List<HadithBookInfo> = emptyList(),
    val filteredBooks: List<HadithBookInfo> = emptyList(),
    val searchQuery: String = "",
    val isSearchActive: Boolean = false,
    val bookmarkedIds: Set<Long> = emptySet(),
    val isLoadingBooks: Boolean = false
)

@HiltViewModel
class HadithViewModel @Inject constructor(
    private val contentRepository: ContentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HadithUiState())
    val uiState: StateFlow<HadithUiState> = _uiState.asStateFlow()

    private val _selectedCollectionKey = MutableStateFlow<String?>(null)
    private val _selectedBookNumber = MutableStateFlow<Int?>(null)
    private val _searchQuery = MutableStateFlow("")

    @OptIn(ExperimentalCoroutinesApi::class)
    val pagedHadiths: Flow<PagingData<ContentItemEntity>> = combine(
        _selectedCollectionKey,
        _selectedBookNumber
    ) { collectionKey, bookNum ->
        Pair(collectionKey, bookNum)
    }.distinctUntilChanged()
        .flatMapLatest { (collectionKey, bookNum) ->
            if (collectionKey != null) {
                contentRepository.getPagedHadiths(collectionKey, bookNum)
            } else {
                kotlinx.coroutines.flow.emptyFlow()
            }
        }.cachedIn(viewModelScope)

    init {
        observeBookmarks()
    }

    private fun observeBookmarks() {
        viewModelScope.launch {
            contentRepository.getAllBookmarks()
                .map { bookmarks -> bookmarks.map { it.contentId }.toSet() }
                .collect { bookmarkedIds ->
                    _uiState.update { it.copy(bookmarkedIds = bookmarkedIds) }
                }
        }
    }

    fun selectCollection(collection: HadithCollectionInfo?) {
        _selectedCollectionKey.value = collection?.key
        _selectedBookNumber.value = null
        _uiState.update {
            it.copy(
                selectedCollection = collection,
                selectedBook = null,
                isLoadingBooks = (collection != null)
            )
        }

        if (collection != null) {
            loadBooks(collection.key)
        }
    }

    private fun loadBooks(collectionKey: String) {
        viewModelScope.launch {
            combine(
                contentRepository.getHadithBooks(collectionKey),
                _searchQuery
            ) { books, query ->
                val filtered = if (query.isBlank()) {
                    books
                } else {
                    books.filter { b ->
                        b.bookName.contains(query, ignoreCase = true) ||
                                b.bookNumber.toString() == query.trim()
                    }
                }
                _uiState.update {
                    it.copy(
                        books = books,
                        filteredBooks = filtered,
                        isLoadingBooks = false,
                        searchQuery = query
                    )
                }
            }.collect {}
        }
    }

    fun selectBook(book: HadithBookInfo?) {
        _selectedBookNumber.value = book?.bookNumber
        _uiState.update { it.copy(selectedBook = book) }
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

    fun navigateBack(): Boolean {
        // If viewing hadith list for a specific book, go back to books list
        if (_uiState.value.selectedBook != null) {
            selectBook(null)
            return true
        }
        // If viewing books list, go back to collections
        if (_uiState.value.selectedCollection != null) {
            selectCollection(null)
            return true
        }
        return false // Let system back handle top level
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
}
