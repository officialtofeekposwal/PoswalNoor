package com.poswal.noor.feature.saved.model

import com.poswal.noor.domain.model.ContentType

enum class SavedTypeFilter(val title: String, val contentType: ContentType) {
    ALL("All", ContentType.BOTH),
    AYAHS("Ayahs", ContentType.QURAN),
    HADITH("Hadith", ContentType.HADITH)
}
