package com.poswal.noor.domain.model

import com.poswal.noor.data.local.entity.ContentItemEntity

enum class AppLanguage(val tag: String, val nativeName: String, val isRtl: Boolean) {
    ENGLISH("en", "English", false),
    HINDI("hi", "हिन्दी", false),
    URDU("ur", "اردو", true),
    ARABIC("ar", "العربية", true);

    companion object {
        fun fromTag(tag: String?): AppLanguage {
            return entries.find { it.tag.equals(tag, ignoreCase = true) } ?: ENGLISH
        }
    }
}

fun ContentItemEntity.translationFor(lang: AppLanguage): String? = when (lang) {
    AppLanguage.ENGLISH -> translationEn
    AppLanguage.HINDI   -> translationHi ?: translationEn
    AppLanguage.URDU    -> translationUr ?: translationEn
    AppLanguage.ARABIC  -> null
}

fun ContentItemEntity.isFallbackTranslation(lang: AppLanguage): Boolean = when (lang) {
    AppLanguage.HINDI -> translationHi == null
    AppLanguage.URDU  -> translationUr == null
    else -> false
}
