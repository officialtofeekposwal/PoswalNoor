package com.poswal.noor.util

import java.util.Calendar
import java.util.Locale
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin
import kotlin.math.tan

data class PrayerTimes(
    val fajr: String,
    val sunrise: String,
    val dhuhr: String,
    val asr: String,
    val maghrib: String,
    val isha: String,
    val nextPrayerName: String,
    val nextPrayerTime: String,
    val minutesUntilNext: Long
)

object PrayerTimesCalculator {

    /**
     * Calculates authentic offline prayer times using solar astronomical algorithms.
     */
    fun calculate(
        calendar: Calendar,
        latitude: Double,
        longitude: Double,
        timezone: Double,
        fajrAngle: Double = 18.0,
        ishaAngle: Double = 18.0,
        isHanafiAsr: Boolean = false
    ): PrayerTimes {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // 1. Julian Date
        val jd = julianDate(year, month, day) - longitude / (360.0 * 24.0)

        // 2. Solar Position
        val d = jd - 2451545.0
        val g = fixAngle(357.529 + 0.98560028 * d)
        val q = fixAngle(280.459 + 0.98564736 * d)
        val l = fixAngle(q + 1.915 * sin(Math.toRadians(g)) + 0.020 * sin(Math.toRadians(2 * g)))

        val e = 23.439 - 0.00000036 * d
        val ra = Math.toDegrees(atan(cos(Math.toRadians(e)) * sin(Math.toRadians(l)) / cos(Math.toRadians(l)))) / 15.0
        val raAdjusted = fixHour(ra)

        val declination = Math.toDegrees(asin(sin(Math.toRadians(e)) * sin(Math.toRadians(l))))
        val eqT = q / 15.0 - fixHour(raAdjusted)

        // Midday (Dhuhr)
        val noon = fixHour(12.0 + timezone - longitude / 15.0 - eqT)

        // Sun Angles
        val sunriseAngle = -0.8333
        val sunriseHours = sunAngleHours(declination, latitude, sunriseAngle)
        val sunrise = noon - sunriseHours
        val sunset = noon + sunriseHours

        val fajrHours = sunAngleHours(declination, latitude, -fajrAngle)
        val fajr = noon - fajrHours

        val ishaHours = sunAngleHours(declination, latitude, -ishaAngle)
        val isha = noon + ishaHours

        // Asr calculation
        val shadowFactor = if (isHanafiAsr) 2.0 else 1.0
        val asrAngle = -Math.toDegrees(atan(1.0 / (shadowFactor + tan(Math.toRadians(Math.abs(latitude - declination))))))
        val asrHours = sunAngleHours(declination, latitude, asrAngle)
        val asr = noon + asrHours

        val maghrib = sunset

        // Formatting
        val fStr = formatTime(fajr)
        val sStr = formatTime(sunrise)
        val dStr = formatTime(noon)
        val aStr = formatTime(asr)
        val mStr = formatTime(maghrib)
        val iStr = formatTime(isha)

        // Next prayer detection
        val currentMinutes = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE)
        val prayers = listOf(
            Triple("Fajr", (fajr * 60).toLong(), fStr),
            Triple("Sunrise", (sunrise * 60).toLong(), sStr),
            Triple("Dhuhr", (noon * 60).toLong(), dStr),
            Triple("Asr", (asr * 60).toLong(), aStr),
            Triple("Maghrib", (maghrib * 60).toLong(), mStr),
            Triple("Isha", (isha * 60).toLong(), iStr)
        )

        var next = prayers.firstOrNull { it.second > currentMinutes }
        val minutesRemaining: Long
        if (next == null) {
            next = prayers.first()
            minutesRemaining = (24 * 60 - currentMinutes) + next.second
        } else {
            minutesRemaining = next.second - currentMinutes
        }

        return PrayerTimes(
            fajr = fStr,
            sunrise = sStr,
            dhuhr = dStr,
            asr = aStr,
            maghrib = mStr,
            isha = iStr,
            nextPrayerName = next.first,
            nextPrayerTime = next.third,
            minutesUntilNext = minutesRemaining
        )
    }

    private fun julianDate(year: Int, month: Int, day: Int): Double {
        var y = year
        var m = month
        if (m <= 2) {
            y -= 1
            m += 12
        }
        val a = floor(y / 100.0)
        val b = 2 - a + floor(a / 4.0)
        return floor(365.25 * (y + 4716)) + floor(30.6001 * (m + 1)) + day + b - 1524.5
    }

    private fun sunAngleHours(declination: Double, latitude: Double, angle: Double): Double {
        val cosH = (sin(Math.toRadians(angle)) - sin(Math.toRadians(latitude)) * sin(Math.toRadians(declination))) /
                (cos(Math.toRadians(latitude)) * cos(Math.toRadians(declination)))
        if (cosH > 1.0) return 0.0
        if (cosH < -1.0) return 12.0
        return Math.toDegrees(acos(cosH)) / 15.0
    }

    private fun fixAngle(a: Double): Double {
        val b = a - 360.0 * floor(a / 360.0)
        return if (b < 0) b + 360.0 else b
    }

    private fun fixHour(h: Double): Double {
        val b = h - 24.0 * floor(h / 24.0)
        return if (b < 0) b + 24.0 else b
    }

    private fun formatTime(hours: Double): String {
        val fixed = fixHour(hours)
        val h = floor(fixed).toInt()
        val m = floor((fixed - h) * 60.0).toInt()
        val amPm = if (h >= 12) "PM" else "AM"
        val h12 = if (h % 12 == 0) 12 else h % 12
        return String.format(Locale.US, "%02d:%02d %s", h12, m, amPm)
    }
}
