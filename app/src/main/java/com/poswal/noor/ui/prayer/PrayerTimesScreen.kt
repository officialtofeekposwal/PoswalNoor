package com.poswal.noor.ui.prayer

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.poswal.noor.ui.components.glass.LiquidGlassCard
import com.poswal.noor.ui.theme.ArabicQuranTextStyle
import com.poswal.noor.ui.theme.NoorEmerald
import com.poswal.noor.ui.theme.NoorEmeraldDeep
import com.poswal.noor.ui.theme.NoorGold
import com.poswal.noor.ui.theme.NoorIvory
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrayerTimesScreen(
    viewModel: PrayerTimesViewModel,
    onNavigateBack: () -> Unit
) {
    val selectedCity by viewModel.selectedCity.collectAsState()
    val prayerTimes by viewModel.prayerTimes.collectAsState()
    val qiblaAngle by viewModel.qiblaAngle.collectAsState()
    val hijriDate by viewModel.hijriDate.collectAsState()
    val isHanafi by viewModel.isHanafi.collectAsState()

    var showCityDropdown by remember { mutableStateOf(false) }
    var deviceAzimuth by remember { mutableFloatStateOf(0f) }

    // Compass Sensor Listener
    val context = LocalContext.current
    DisposableEffect(Unit) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event?.sensor?.type == Sensor.TYPE_ROTATION_VECTOR) {
                    val rotationMatrix = FloatArray(9)
                    SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                    val orientation = FloatArray(3)
                    SensorManager.getOrientation(rotationMatrix, orientation)
                    val azimuthInDegrees = (Math.toDegrees(orientation[0].toDouble()).toFloat() + 360) % 360
                    deviceAzimuth = azimuthInDegrees
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        if (rotationSensor != null) {
            sensorManager.registerListener(listener, rotationSensor, SensorManager.SENSOR_DELAY_UI)
        }

        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }

    val qiblaDifference = (qiblaAngle - deviceAzimuth + 360) % 360
    val isAligned = abs(qiblaDifference) < 4.0 || abs(qiblaDifference - 360) < 4.0

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Prayer Times & Qibla",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "${selectedCity.name}, ${selectedCity.country} (100% Offline)",
                            style = MaterialTheme.typography.labelSmall,
                            color = NoorEmerald
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                actions = {
                    Box {
                        IconButton(onClick = { showCityDropdown = true }) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Select City",
                                tint = NoorEmerald
                            )
                        }
                        DropdownMenu(
                            expanded = showCityDropdown,
                            onDismissRequest = { showCityDropdown = false }
                        ) {
                            viewModel.cities.forEach { city ->
                                DropdownMenuItem(
                                    text = { Text("${city.name}, ${city.country}") },
                                    onClick = {
                                        viewModel.selectCity(city)
                                        showCityDropdown = false
                                    }
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Hijri Date Card
            item {
                LiquidGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = 16.dp
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = null,
                                tint = NoorGold,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = hijriDate.formattedDate,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${hijriDate.day} ${hijriDate.monthNameAr} ${hijriDate.year} هـ",
                                    style = ArabicQuranTextStyle,
                                    fontSize = 16.sp,
                                    color = NoorEmerald
                                )
                            }
                        }
                    }
                }
            }

            // 2. Next Prayer Countdown Hero Card
            item {
                LiquidGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = 20.dp
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "UPCOMING PRAYER",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = NoorGold,
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = prayerTimes.nextPrayerName,
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = "at ${prayerTimes.nextPrayerTime}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = NoorEmerald
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        val hours = prayerTimes.minutesUntilNext / 60
                        val mins = prayerTimes.minutesUntilNext % 60
                        val countdownText = if (hours > 0) "${hours}h ${mins}m remaining" else "${mins} minutes remaining"

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(NoorEmerald.copy(alpha = 0.12f))
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = countdownText,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = NoorEmerald
                            )
                        }
                    }
                }
            }

            // 3. Prayer Times Timetable Card
            item {
                LiquidGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = 18.dp
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "DAILY SCHEDULE",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = NoorEmerald,
                                letterSpacing = 1.sp
                            )

                            Text(
                                text = if (isHanafi) "Asr: Hanafi" else "Asr: Standard",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = NoorGold,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(NoorGold.copy(alpha = 0.15f))
                                    .clickable { viewModel.toggleAsrMethod() }
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        val schedule = listOf(
                            Triple("Fajr (الفجر)", prayerTimes.fajr, prayerTimes.nextPrayerName == "Fajr"),
                            Triple("Sunrise (الشروق)", prayerTimes.sunrise, prayerTimes.nextPrayerName == "Sunrise"),
                            Triple("Dhuhr (الظهر)", prayerTimes.dhuhr, prayerTimes.nextPrayerName == "Dhuhr"),
                            Triple("Asr (العصر)", prayerTimes.asr, prayerTimes.nextPrayerName == "Asr"),
                            Triple("Maghrib (المغرب)", prayerTimes.maghrib, prayerTimes.nextPrayerName == "Maghrib"),
                            Triple("Isha (العشاء)", prayerTimes.isha, prayerTimes.nextPrayerName == "Isha")
                        )

                        schedule.forEach { (name, time, isNext) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isNext) NoorEmerald.copy(alpha = 0.12f) else Color.Transparent)
                                    .padding(horizontal = 8.dp, vertical = 7.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (isNext) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isNext) NoorEmerald else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = time,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = if (isNext) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isNext) NoorEmerald else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // 4. Qibla Compass Dial Card
            item {
                LiquidGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = 20.dp
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "OFFLINE QIBLA COMPASS",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = NoorEmerald,
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = if (isAligned) "Aligned with Kaaba ✓" else "${qiblaAngle.toInt()}° from North",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isAligned) NoorEmerald else MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Visual Animated Compass
                        Box(
                            modifier = Modifier
                                .size(240.dp)
                                .clip(CircleShape)
                                .background(NoorEmeraldDeep.copy(alpha = 0.08f))
                                .border(
                                    width = 2.dp,
                                    color = if (isAligned) NoorEmerald else NoorGold.copy(alpha = 0.3f),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            // Dial Markings
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val center = this.center
                                val radius = size.minDimension / 2f
                                drawCircle(
                                    color = Color.White.copy(alpha = 0.05f),
                                    radius = radius - 8.dp.toPx()
                                )
                            }

                            // Rotating Needle to Qibla
                            val animatedNeedleRotation by animateFloatAsState(
                                targetValue = qiblaDifference,
                                animationSpec = tween(150),
                                label = "compassNeedle"
                            )

                            Icon(
                                imageVector = Icons.Default.Navigation,
                                contentDescription = "Qibla Needle",
                                tint = if (isAligned) NoorEmerald else NoorGold,
                                modifier = Modifier
                                    .size(72.dp)
                                    .rotate(animatedNeedleRotation)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Rotate your phone until the needle points straight up.",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // 5. Major Islamic Occasions
            item {
                Text(
                    text = "UPCOMING ISLAMIC OCCASIONS",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = NoorEmerald,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            items(viewModel.occasions) { occasion ->
                LiquidGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = 14.dp
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = occasion.title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = occasion.description,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                        Text(
                            text = occasion.hijriDate,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = NoorGold,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(NoorGold.copy(alpha = 0.12f))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}
