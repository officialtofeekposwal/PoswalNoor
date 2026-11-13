package com.poswal.noor.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.poswal.noor.data.local.entity.ContentItemEntity
import com.poswal.noor.data.preferences.UserPreferencesRepository
import com.poswal.noor.domain.model.AppLanguage
import com.poswal.noor.domain.repository.ContentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DetailUiState(
    val item: ContentItemEntity? = null,
    val language: AppLanguage = AppLanguage.ENGLISH,
    val fontSizeScale: Float = 1.0f,
    val isLoading: Boolean = false
)

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val contentRepository: ContentRepository,
    private val preferencesRepository: UserPreferencesRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val contentId: Long = checkNotNull(savedStateHandle["contentId"]) {
        "contentId is required for DetailViewModel"
    }.toString().toLong()

    private val _isLoading = MutableStateFlow(true)
    private val _itemFlow = contentRepository.getContentById(contentId)

    val uiState: StateFlow<DetailUiState> = combine(
        _itemFlow,
        preferencesRepository.userPreferencesFlow,
        _isLoading
    ) { item, prefs, loading ->
        DetailUiState(
            item = item,
            language = prefs.language,
            fontSizeScale = prefs.fontSizeScale,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DetailUiState()
    )

    fun toggleFavorite() {
        val current = uiState.value.item ?: return
        viewModelScope.launch {
            contentRepository.toggleFavorite(current.id, !current.isFavorite)
        }
    }
}
