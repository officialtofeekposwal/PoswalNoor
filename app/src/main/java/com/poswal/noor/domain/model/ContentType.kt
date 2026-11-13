package com.poswal.noor.domain.model

enum class ContentType(val key: String) {
    BOTH("both"),
    QURAN("quran"),
    HADITH("hadith");

    companion object {
        fun fromKey(key: String?): ContentType =
            entries.find { it.key == key } ?: BOTH
    }
}
