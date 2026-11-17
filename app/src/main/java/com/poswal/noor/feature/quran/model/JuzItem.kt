package com.poswal.noor.feature.quran.model

data class JuzItem(
    val number: Int,
    val nameArabic: String,
    val nameEnglish: String,
    val startSurahNumber: Int,
    val startSurahName: String,
    val startAyah: Int,
    val endSurahNumber: Int,
    val endSurahName: String,
    val endAyah: Int,
    val totalVerses: Int
)

object QuranJuzData {
    val ALL_JUZ: List<JuzItem> = listOf(
        JuzItem(1, "الم", "Alif Lam Meem", 1, "Al-Fatihah", 1, 2, "Al-Baqarah", 141, 148),
        JuzItem(2, "سَيَقُولُ", "Sayaqool", 2, "Al-Baqarah", 142, 2, "Al-Baqarah", 252, 111),
        JuzItem(3, "تِلْكَ الرُّسُلُ", "Tilka-r-Rusul", 2, "Al-Baqarah", 253, 3, "Ali 'Imran", 92, 126),
        JuzItem(4, "لَنْ تَنَالُوا", "Lan Tanaaloo", 3, "Ali 'Imran", 93, 4, "An-Nisa", 23, 131),
        JuzItem(5, "وَالْمُحْصَنَاتُ", "Wal-Muhsanat", 4, "An-Nisa", 24, 4, "An-Nisa", 147, 124),
        JuzItem(6, "لَا يُحِبُّ اللَّهُ", "La Yuhibbullah", 4, "An-Nisa", 148, 5, "Al-Ma'idah", 81, 110),
        JuzItem(7, "وَإِذَا سَمِعُوا", "Wa Iza Sami'oo", 5, "Al-Ma'idah", 82, 6, "Al-An'am", 110, 149),
        JuzItem(8, "وَلَوْ أَنَّنَا", "Wa Law Annana", 6, "Al-An'am", 111, 7, "Al-A'raf", 87, 142),
        JuzItem(9, "قَالَ الْمَلَأُ", "Qal-al-Mala'u", 7, "Al-A'raf", 88, 8, "Al-Anfal", 40, 159),
        JuzItem(10, "وَاعْلَمُوا", "Wa'lamoo", 8, "Al-Anfal", 41, 9, "At-Tawbah", 92, 127),
        JuzItem(11, "يَعْتَذِرُونَ", "Ya'taziroon", 9, "At-Tawbah", 93, 11, "Hud", 5, 151),
        JuzItem(12, "وَمَا مِنْ دَابَّةٍ", "Wa Ma Min Dabbatin", 11, "Hud", 6, 12, "Yusuf", 52, 170),
        JuzItem(13, "وَمَا أُبَرِّئُ", "Wa Ma Ubarri'u", 12, "Yusuf", 53, 14, "Ibrahim", 52, 155),
        JuzItem(14, "رُبَمَا", "Rubama", 15, "Al-Hijr", 1, 16, "An-Nahl", 128, 227),
        JuzItem(15, "سُبْحَانَ الَّذِي", "Subhanallazi", 17, "Al-Isra", 1, 18, "Al-Kahf", 74, 185),
        JuzItem(16, "قَالَ أَلَمْ", "Qala Alam", 18, "Al-Kahf", 75, 20, "Taha", 135, 269),
        JuzItem(17, "اقْتَرَبَ لِلنَّاسِ", "Iqtaraba Lin-Nasi", 21, "Al-Anbiya", 1, 22, "Al-Hajj", 78, 190),
        JuzItem(18, "قَدْ أَفْلَحَ", "Qad Aflaha", 23, "Al-Mu'minun", 1, 25, "Al-Furqan", 20, 202),
        JuzItem(19, "وَقَالَ الَّذِينَ", "Wa Qalallazina", 25, "Al-Furqan", 21, 27, "An-Naml", 55, 339),
        JuzItem(20, "أَمَّنْ خَلَقَ", "Amman Khalaqa", 27, "An-Naml", 56, 29, "Al-'Ankabut", 45, 171),
        JuzItem(21, "اتْلُ مَا أُوحِيَ", "Utlu Ma Oohiya", 29, "Al-'Ankabut", 46, 33, "Al-Ahzab", 30, 178),
        JuzItem(22, "وَمَنْ يَقْنُتْ", "Wa Man Yaqnut", 33, "Al-Ahzab", 31, 36, "Ya-Sin", 27, 169),
        JuzItem(23, "وَمَا لِيَ", "Wa Maliya", 36, "Ya-Sin", 28, 39, "Az-Zumar", 31, 357),
        JuzItem(24, "فَمَنْ أَظْلَمُ", "Faman Azlamu", 39, "Az-Zumar", 32, 41, "Fussilat", 46, 175),
        JuzItem(25, "إِلَيْهِ يُرَدُّ", "Ilayhi Yuraddu", 41, "Fussilat", 47, 45, "Al-Jathiyah", 37, 246),
        JuzItem(26, "حم", "Ha-Meem", 46, "Al-Ahqaf", 1, 51, "Adh-Dhariyat", 30, 195),
        JuzItem(27, "قَالَ فَمَا خَطْبُكُمْ", "Qala Fama Khatbukum", 51, "Adh-Dhariyat", 31, 57, "Al-Hadid", 29, 399),
        JuzItem(28, "قَدْ سَمِعَ اللَّهُ", "Qad Sami'allah", 58, "Al-Mujadilah", 1, 66, "At-Tahrim", 12, 137),
        JuzItem(29, "تَبَارَكَ الَّذِي", "Tabarakallazi", 67, "Al-Mulk", 1, 77, "Al-Mursalat", 50, 431),
        JuzItem(30, "عَمَّ يَتَسَاءَلُونَ", "'Amma Yatasa'aloon", 78, "An-Naba", 1, 114, "An-Nas", 6, 564)
    )
}
