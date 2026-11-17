package com.poswal.noor.ui.hadith

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.poswal.noor.data.local.entity.ContentItemEntity
import com.poswal.noor.data.preferences.UserPreferencesRepository
import com.poswal.noor.domain.model.AppLanguage
import com.poswal.noor.domain.repository.ContentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HadithViewModel @Inject constructor(
    private val contentRepository: ContentRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val collections = listOf("Sahih al-Bukhari", "Sahih Muslim")

    private val _selectedCollection = MutableStateFlow("Sahih al-Bukhari")
    val selectedCollection: StateFlow<String> = _selectedCollection.asStateFlow()

    val pagedHadiths: Flow<PagingData<ContentItemEntity>> = _selectedCollection
        .flatMapLatest { collection ->
            contentRepository.getPagedHadiths(collection)
        }
        .cachedIn(viewModelScope)

    val language: StateFlow<AppLanguage> = userPreferencesRepository.userPreferencesFlow
        .map { it.language }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppLanguage.ENGLISH
        )

    fun selectCollection(collection: String) {
        _selectedCollection.value = collection
    }

    fun isBookmarked(contentId: Long): Flow<Boolean> = contentRepository.isBookmarked(contentId)

    fun toggleBookmark(contentId: Long) {
        viewModelScope.launch {
            contentRepository.toggleBookmark(contentId, folder = "Hadith")
        }
    }
}
