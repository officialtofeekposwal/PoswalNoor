package com.poswal.noor.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.poswal.noor.R

// Bundled Offline Typography
val AmiriFontFamily = FontFamily(
    Font(R.font.amiri_regular, FontWeight.Normal),
    Font(R.font.amiri_bold, FontWeight.Bold)
)

val NotoNastaliqUrduFamily = FontFamily(
    Font(R.font.noto_nastaliq_urdu, FontWeight.Normal)
)

val NotoSansDevanagariFamily = FontFamily(
    Font(R.font.noto_sans_devanagari, FontWeight.Normal)
)

val QuranArabicFontFamily = AmiriFontFamily
val HeadingFontFamily = FontFamily.Serif
val BodyFontFamily = FontFamily.Default

val NoorTypography = Typography(
    headlineLarge = TextStyle(
        fontFamily = HeadingFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 36.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = HeadingFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.sp
    ),
    titleLarge = TextStyle(
        fontFamily = HeadingFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 28.sp
    ),
    titleMedium = TextStyle(
        fontFamily = BodyFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = BodyFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = BodyFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    labelLarge = TextStyle(
        fontFamily = BodyFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    labelSmall = TextStyle(
        fontFamily = BodyFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp
    )
)

// Specific Style for Arabic Quran & Hadith Text (Offline Amiri)
val ArabicQuranTextStyle = TextStyle(
    fontFamily = QuranArabicFontFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 25.sp,
    lineHeight = 48.sp
)

// Specific Style for Urdu Translation (Offline Noto Nastaliq Urdu)
val UrduTranslationTextStyle = TextStyle(
    fontFamily = NotoNastaliqUrduFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 18.sp,
    lineHeight = 36.sp
)

// Specific Style for Hindi Translation (Offline Noto Sans Devanagari)
val HindiTranslationTextStyle = TextStyle(
    fontFamily = NotoSansDevanagariFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 16.sp,
    lineHeight = 26.sp
)
