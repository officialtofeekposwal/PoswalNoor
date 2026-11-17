package com.poswal.noor.ui.tasbih

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.poswal.noor.data.preferences.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DhikrPreset(
    val titleAr: String,
    val titleEn: String,
    val translation: String,
    val target: Int
)

@HiltViewModel
class TasbihViewModel @Inject constructor(
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val presets = listOf(
        DhikrPreset("سُبْحَانَ اللَّهِ", "SubhanAllah", "Glory be to Allah", 33),
        DhikrPreset("الْحَمْدُ لِلَّهِ", "Alhamdulillah", "Praise be to Allah", 33),
        DhikrPreset("اللَّهُ أَكْبَرُ", "Allahu Akbar", "Allah is the Greatest", 34),
        DhikrPreset("أَسْتَغْفِرُ اللَّهَ", "Astaghfirullah", "I seek forgiveness from Allah", 100),
        DhikrPreset("لَا إِلَٰهَ إِلَّا اللَّهُ", "La ilaha illallah", "None has the right to be worshipped but Allah", 100),
        DhikrPreset("سُبْحَانَ اللَّهِ وَبِحَمْدِهِ", "SubhanAllahi wa bihamdihi", "Glory and praise be to Allah", 100)
    )

    private val _selectedPreset = MutableStateFlow(presets.first())
    val selectedPreset: StateFlow<DhikrPreset> = _selectedPreset.asStateFlow()

    private val _currentCount = MutableStateFlow(0)
    val currentCount: StateFlow<Int> = _currentCount.asStateFlow()

    private val _totalLifetimeCount = MutableStateFlow(0)
    val totalLifetimeCount: StateFlow<Int> = _totalLifetimeCount.asStateFlow()

    fun selectPreset(preset: DhikrPreset) {
        _selectedPreset.value = preset
        _currentCount.value = 0
    }

    fun increment() {
        val target = _selectedPreset.value.target
        _currentCount.value = if (_currentCount.value >= target) 1 else _currentCount.value + 1
        _totalLifetimeCount.value += 1
    }

    fun reset() {
        _currentCount.value = 0
    }
}
