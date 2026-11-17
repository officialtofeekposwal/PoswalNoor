package com.poswal.noor.util

import java.util.Calendar
import kotlin.math.floor

data class HijriDate(
    val day: Int,
    val monthNumber: Int,
    val monthNameEn: String,
    val monthNameAr: String,
    val year: Int,
    val formattedDate: String
)

data class IslamicOccasion(
    val title: String,
    val hijriDate: String,
    val description: String
)

object HijriCalendarHelper {

    val HIJRI_MONTHS_EN = listOf(
        "Muharram", "Safar", "Rabi' al-Awwal", "Rabi' al-Thani",
        "Jumada al-Awwal", "Jumada al-Thani", "Rajab", "Sha'ban",
        "Ramadan", "Shawwal", "Dhu al-Qi'dah", "Dhu al-Hijjah"
    )

    val HIJRI_MONTHS_AR = listOf(
        "المحرّم", "صفر", "ربيع الأول", "ربيع الآخر",
        "جمادى الأولى", "جمادى الآخرة", "رجب", "شعبان",
        "رمضان", "شوّال", "ذو القعدة", "ذو الحجة"
    )

    /**
     * Converts a Gregorian calendar instance into an algorithmic Hijri date.
     */
    fun getHijriDate(calendar: Calendar = Calendar.getInstance()): HijriDate {
        val y = calendar.get(Calendar.YEAR)
        val m = calendar.get(Calendar.MONTH) + 1
        val d = calendar.get(Calendar.DAY_OF_MONTH)

        var jd = (1461 * (y + 4800 + (m - 14) / 12)) / 4 +
                (367 * (m - 2 - 12 * ((m - 14) / 12))) / 12 -
                (3 * ((y + 4900 + (m - 14) / 12) / 100)) / 4 +
                d - 32075

        val l = jd - 1948440 + 10632
        val n = (l - 1) / 10631
        val l1 = l - 10631 * n + 354
        val j = ((10985 - l1) / 5316) * ((50 * l1) / 17719) + (l1 / 5670) * ((43 * l1) / 15238)
        val l2 = l1 - ((30 - j) / 15) * ((17719 * j) / 50) - (j / 16) * ((15238 * j) / 43) + 29
        val month = (24 * l2) / 709
        val day = l2 - (709 * month) / 24
        val year = 30 * n + j - 30

        val mIndex = (month - 1).coerceIn(0, 11)
        val mNameEn = HIJRI_MONTHS_EN[mIndex]
        val mNameAr = HIJRI_MONTHS_AR[mIndex]

        return HijriDate(
            day = day,
            monthNumber = month,
            monthNameEn = mNameEn,
            monthNameAr = mNameAr,
            year = year,
            formattedDate = "$day $mNameEn $year AH"
        )
    }

    val ISLAMIC_OCCASIONS = listOf(
        IslamicOccasion("Islamic New Year", "1 Muharram", "First day of the Hijri calendar year"),
        IslamicOccasion("Day of Ashura", "10 Muharram", "Day of fasting and commemoration of Prophet Musa (AS)"),
        IslamicOccasion("Mawlid an-Nabi", "12 Rabi' al-Awwal", "Observance of the birth of Prophet Muhammad (ﷺ)"),
        IslamicOccasion("Isra and Mi'raj", "27 Rajab", "The miraculous Night Journey and Ascension to Heaven"),
        IslamicOccasion("Night of Bara'ah", "15 Sha'ban", "Night of forgiveness preceding Ramadan"),
        IslamicOccasion("First Day of Ramadan", "1 Ramadan", "Beginning of the blessed month of fasting"),
        IslamicOccasion("Laylat al-Qadr", "27 Ramadan (approx)", "The Night of Power, better than a thousand months"),
        IslamicOccasion("Eid al-Fitr", "1 Shawwal", "Celebration feast marking the end of Ramadan"),
        IslamicOccasion("Day of Arafah", "9 Dhu al-Hijjah", "The pinnacle day of the Hajj pilgrimage"),
        IslamicOccasion("Eid al-Adha", "10 Dhu al-Hijjah", "Feast of the Sacrifice commemorating Prophet Ibrahim (AS)")
    )
}
