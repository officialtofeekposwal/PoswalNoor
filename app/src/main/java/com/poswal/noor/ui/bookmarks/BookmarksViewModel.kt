package com.poswal.noor.ui.bookmarks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.poswal.noor.data.local.entity.BookmarkEntity
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject

data class BookmarkWithContent(
    val bookmark: BookmarkEntity,
    val content: ContentItemEntity
)

@HiltViewModel
class BookmarksViewModel @Inject constructor(
    private val contentRepository: ContentRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _selectedFolder = MutableStateFlow("All")
    val selectedFolder: StateFlow<String> = _selectedFolder.asStateFlow()

    val folders: StateFlow<List<String>> = contentRepository.getAllBookmarkFolders()
        .map { listOf("All") + it.filter { f -> f != "All" } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = listOf("All")
        )

    val language: StateFlow<AppLanguage> = userPreferencesRepository.userPreferencesFlow
        .map { it.language }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppLanguage.ENGLISH
        )

    val bookmarkedItems: StateFlow<List<BookmarkWithContent>> = combine(
        contentRepository.getAllBookmarks(),
        _selectedFolder
    ) { bookmarks, folder ->
        val filtered = if (folder == "All") bookmarks else bookmarks.filter { it.folder == folder }
        filtered.mapNotNull { bm ->
            val content = contentRepository.getContentByIdSync(bm.contentId)
            if (content != null) BookmarkWithContent(bm, content) else null
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun selectFolder(folder: String) {
        _selectedFolder.value = folder
    }

    fun updateNote(contentId: Long, note: String?) {
        viewModelScope.launch {
            contentRepository.updateBookmarkNote(contentId, note)
        }
    }

    fun removeBookmark(contentId: Long) {
        viewModelScope.launch {
            contentRepository.toggleBookmark(contentId)
        }
    }

    suspend fun exportBookmarksJson(): String {
        val bookmarks = contentRepository.getAllBookmarks().first()
        val jsonArray = JSONArray()
        for (b in bookmarks) {
            val obj = JSONObject()
            obj.put("contentId", b.contentId)
            obj.put("folder", b.folder)
            obj.put("note", b.note ?: "")
            obj.put("createdAt", b.createdAt)
            jsonArray.put(obj)
        }
        return jsonArray.toString(2)
    }

    fun importBookmarksJson(jsonString: String) {
        viewModelScope.launch {
            try {
                val array = JSONArray(jsonString)
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    val contentId = obj.getLong("contentId")
                    val folder = obj.optString("folder", "Imported")
                    val note = obj.optString("note", "").takeIf { it.isNotBlank() }
                    contentRepository.toggleBookmark(contentId, folder = folder, note = note)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
