package com.poswal.noor.util

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan

data class OfflineCity(
    val name: String,
    val country: String,
    val latitude: Double,
    val longitude: Double,
    val timezoneOffsetHours: Double
)

object QiblaCalculator {

    const val KAABA_LAT = 21.422487
    const val KAABA_LNG = 39.826206

    /**
     * Calculates the Qibla bearing from True North in degrees [0, 360).
     */
    fun calculateQiblaDirection(userLat: Double, userLng: Double): Float {
        val userLatRad = Math.toRadians(userLat)
        val userLngRad = Math.toRadians(userLng)
        val kaabaLatRad = Math.toRadians(KAABA_LAT)
        val kaabaLngRad = Math.toRadians(KAABA_LNG)

        val deltaLng = kaabaLngRad - userLngRad

        val y = sin(deltaLng)
        val x = cos(userLatRad) * tan(kaabaLatRad) - sin(userLatRad) * cos(deltaLng)

        val bearingRad = atan2(y, x)
        val bearingDeg = Math.toDegrees(bearingRad)

        return ((bearingDeg + 360.0) % 360.0).toFloat()
    }

    /**
     * Comprehensive offline database of major cities.
     * Allows 100% offline Qibla & Prayer calculations with zero permissions.
     */
    val OFFLINE_CITIES = listOf(
        OfflineCity("New Delhi", "India", 28.6139, 77.2090, 5.5),
        OfflineCity("Mumbai", "India", 19.0760, 72.8777, 5.5),
        OfflineCity("Srinagar", "India", 34.0837, 74.7973, 5.5),
        OfflineCity("Hyderabad", "India", 17.3850, 78.4867, 5.5),
        OfflineCity("Bengaluru", "India", 12.9716, 77.5946, 5.5),
        OfflineCity("Kolkata", "India", 22.5726, 88.3639, 5.5),
        OfflineCity("Lucknow", "India", 26.8467, 80.9462, 5.5),
        OfflineCity("Karachi", "Pakistan", 24.8607, 67.0011, 5.0),
        OfflineCity("Lahore", "Pakistan", 31.5204, 74.3587, 5.0),
        OfflineCity("Islamabad", "Pakistan", 33.6844, 73.0479, 5.0),
        OfflineCity("Dhaka", "Bangladesh", 23.8103, 90.4125, 6.0),
        OfflineCity("Chittagong", "Bangladesh", 22.3569, 91.7832, 6.0),
        OfflineCity("Dubai", "UAE", 25.2048, 55.2708, 4.0),
        OfflineCity("Abu Dhabi", "UAE", 24.4539, 54.3773, 4.0),
        OfflineCity("Mecca", "Saudi Arabia", 21.3891, 39.8579, 3.0),
        OfflineCity("Medina", "Saudi Arabia", 24.5247, 39.5692, 3.0),
        OfflineCity("Riyadh", "Saudi Arabia", 24.7136, 46.6753, 3.0),
        OfflineCity("Jeddah", "Saudi Arabia", 21.4858, 39.1925, 3.0),
        OfflineCity("Istanbul", "Turkey", 41.0082, 28.9784, 3.0),
        OfflineCity("Ankara", "Turkey", 39.9334, 32.8597, 3.0),
        OfflineCity("Cairo", "Egypt", 30.0444, 31.2357, 2.0),
        OfflineCity("Alexandria", "Egypt", 31.2001, 29.9187, 2.0),
        OfflineCity("Jakarta", "Indonesia", -6.2088, 106.8456, 7.0),
        OfflineCity("Surabaya", "Indonesia", -7.2575, 112.7521, 7.0),
        OfflineCity("Kuala Lumpur", "Malaysia", 3.1390, 101.6869, 8.0),
        OfflineCity("Singapore", "Singapore", 1.3521, 103.8198, 8.0),
        OfflineCity("London", "United Kingdom", 51.5074, -0.1278, 0.0),
        OfflineCity("Birmingham", "United Kingdom", 52.4862, -1.8904, 0.0),
        OfflineCity("Manchester", "United Kingdom", 53.4808, -2.2426, 0.0),
        OfflineCity("New York", "USA", 40.7128, -74.0060, -5.0),
        OfflineCity("Chicago", "USA", 41.8781, -87.6298, -6.0),
        OfflineCity("Houston", "USA", 29.7604, -95.3698, -6.0),
        OfflineCity("Los Angeles", "USA", 34.0522, -118.2437, -8.0),
        OfflineCity("Toronto", "Canada", 43.6532, -79.3832, -5.0),
        OfflineCity("Paris", "France", 48.8566, 2.3522, 1.0),
        OfflineCity("Berlin", "Germany", 52.5200, 13.4050, 1.0),
        OfflineCity("Sydney", "Australia", -33.8688, 151.2093, 10.0),
        OfflineCity("Melbourne", "Australia", -37.8136, 144.9631, 10.0)
    )
}
