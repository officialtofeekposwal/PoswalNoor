package com.poswal.noor.feature.saved

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.poswal.noor.domain.model.SavedItem
import com.poswal.noor.domain.repository.ContentRepository
import com.poswal.noor.feature.saved.model.SavedTypeFilter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SavedUiState(
    val items: List<SavedItem> = emptyList(),
    val totalCount: Int = 0,
    val collections: List<String> = emptyList(),
    val selectedType: SavedTypeFilter = SavedTypeFilter.ALL,
    val selectedCollection: String? = null,
    val searchQuery: String = "",
    val hasNotesOnly: Boolean = false,
    val isLoading: Boolean = true,
    val userMessage: String? = null
)

private data class FilterParams(
    val type: SavedTypeFilter,
    val collection: String?,
    val query: String,
    val notesOnly: Boolean,
    val message: String?
)

@HiltViewModel
class SavedViewModel @Inject constructor(
    private val repository: ContentRepository
) : ViewModel() {

    private val _selectedType = MutableStateFlow(SavedTypeFilter.ALL)
    private val _selectedCollection = MutableStateFlow<String?>(null)
    private val _searchQuery = MutableStateFlow("")
    private val _hasNotesOnly = MutableStateFlow(false)
    private val _userMessage = MutableStateFlow<String?>(null)

    private val filterParams = combine(
        _selectedType,
        _selectedCollection,
        _searchQuery,
        _hasNotesOnly,
        _userMessage
    ) { type, collection, query, notesOnly, message ->
        FilterParams(type, collection, query, notesOnly, message)
    }

    val uiState: StateFlow<SavedUiState> = combine(
        repository.getSavedItems(),
        repository.getUserCollectionNames(),
        filterParams
    ) { rawItems, customCollections, params ->

        val allCollections = listOf("Default") + customCollections.filter { it != "Default" }

        val filtered = rawItems.filter { item ->
            // 1. Type filter
            val matchesType = when (params.type) {
                SavedTypeFilter.ALL -> true
                SavedTypeFilter.AYAHS -> item.content.type.equals("quran", ignoreCase = true)
                SavedTypeFilter.HADITH -> item.content.type.equals("hadith", ignoreCase = true)
            }

            // 2. Collection filter
            val matchesCollection = params.collection == null || item.bookmark.folder == params.collection

            // 3. Notes only filter
            val matchesNotesOnly = !params.notesOnly || !item.bookmark.note.isNullOrBlank()

            // 4. Offline search query
            val trimmedQuery = params.query.trim()
            val matchesQuery = if (trimmedQuery.isBlank()) {
                true
            } else {
                item.content.reference.contains(trimmedQuery, ignoreCase = true) ||
                        item.content.translationEn.contains(trimmedQuery, ignoreCase = true) ||
                        item.content.arabic.contains(trimmedQuery, ignoreCase = true) ||
                        (item.bookmark.note?.contains(trimmedQuery, ignoreCase = true) == true)
            }

            matchesType && matchesCollection && matchesNotesOnly && matchesQuery
        }

        SavedUiState(
            items = filtered,
            totalCount = rawItems.size,
            collections = allCollections,
            selectedType = params.type,
            selectedCollection = params.collection,
            searchQuery = params.query,
            hasNotesOnly = params.notesOnly,
            isLoading = false,
            userMessage = params.message
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SavedUiState()
    )

    fun setTypeFilter(type: SavedTypeFilter) {
        _selectedType.value = type
    }

    fun setCollectionFilter(collection: String?) {
        _selectedCollection.value = collection
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleNotesOnly() {
        _hasNotesOnly.update { !it }
    }

    fun createCollection(name: String) {
        viewModelScope.launch {
            val created = repository.createUserCollection(name)
            if (created) {
                _selectedCollection.value = name
                _userMessage.value = "Collection '$name' created"
            } else {
                _userMessage.value = "Collection '$name' already exists"
            }
        }
    }

    fun deleteCollection(name: String) {
        viewModelScope.launch {
            repository.deleteUserCollection(name)
            if (_selectedCollection.value == name) {
                _selectedCollection.value = null
            }
            _userMessage.value = "Collection '$name' deleted"
        }
    }

    fun updateNote(contentId: Long, note: String?) {
        viewModelScope.launch {
            repository.updateBookmarkNote(contentId, note)
            _userMessage.value = if (note.isNullOrBlank()) "Reflection removed" else "Reflection saved"
        }
    }

    fun moveItemToCollection(contentId: Long, folder: String) {
        viewModelScope.launch {
            repository.updateBookmarkFolder(contentId, folder)
            _userMessage.value = "Moved to '$folder'"
        }
    }

    fun removeBookmark(contentId: Long) {
        viewModelScope.launch {
            repository.toggleBookmark(contentId)
            _userMessage.value = "Bookmark removed"
        }
    }

    fun exportBookmarksJson(onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            val json = repository.exportBookmarksJson()
            onSuccess(json)
        }
    }

    fun importBookmarksJson(jsonString: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = repository.importBookmarksJson(jsonString)
            result.onSuccess { (bookmarks, collections) ->
                val msg = "Imported $bookmarks bookmarks and $collections collections"
                _userMessage.value = msg
                onResult(true, msg)
            }.onFailure { err ->
                val errorMsg = "Import failed: ${err.localizedMessage ?: "Invalid JSON format"}"
                _userMessage.value = errorMsg
                onResult(false, errorMsg)
            }
        }
    }

    fun clearUserMessage() {
        _userMessage.value = null
    }
}
