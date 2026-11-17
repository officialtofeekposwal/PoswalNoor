package com.poswal.noor.ui.prayer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.poswal.noor.util.HijriCalendarHelper
import com.poswal.noor.util.HijriDate
import com.poswal.noor.util.IslamicOccasion
import com.poswal.noor.util.OfflineCity
import com.poswal.noor.util.PrayerTimes
import com.poswal.noor.util.PrayerTimesCalculator
import com.poswal.noor.util.QiblaCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class PrayerTimesViewModel @Inject constructor() : ViewModel() {

    val cities = QiblaCalculator.OFFLINE_CITIES

    private val _selectedCity = MutableStateFlow(cities.first())
    val selectedCity: StateFlow<OfflineCity> = _selectedCity.asStateFlow()

    private val _isHanafi = MutableStateFlow(false)
    val isHanafi: StateFlow<Boolean> = _isHanafi.asStateFlow()

    val hijriDate: StateFlow<HijriDate> = MutableStateFlow(HijriCalendarHelper.getHijriDate()).asStateFlow()
    val occasions: List<IslamicOccasion> = HijriCalendarHelper.ISLAMIC_OCCASIONS

    val prayerTimes: StateFlow<PrayerTimes> = combine(_selectedCity, _isHanafi) { city, hanafi ->
        PrayerTimesCalculator.calculate(
            calendar = Calendar.getInstance(),
            latitude = city.latitude,
            longitude = city.longitude,
            timezone = city.timezoneOffsetHours,
            isHanafiAsr = hanafi
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PrayerTimesCalculator.calculate(
            calendar = Calendar.getInstance(),
            latitude = cities.first().latitude,
            longitude = cities.first().longitude,
            timezone = cities.first().timezoneOffsetHours
        )
    )

    val qiblaAngle: StateFlow<Float> = _selectedCity.combine(_selectedCity) { city, _ ->
        QiblaCalculator.calculateQiblaDirection(city.latitude, city.longitude)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = QiblaCalculator.calculateQiblaDirection(cities.first().latitude, cities.first().longitude)
    )

    fun selectCity(city: OfflineCity) {
        _selectedCity.value = city
    }

    fun toggleAsrMethod() {
        _isHanafi.value = !_isHanafi.value
    }
}
