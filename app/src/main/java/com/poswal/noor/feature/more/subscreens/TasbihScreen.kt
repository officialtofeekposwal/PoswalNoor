package com.poswal.noor.feature.more.subscreens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.poswal.noor.core.designsystem.NoorShapes
import com.poswal.noor.core.designsystem.NoorSpacing
import com.poswal.noor.core.designsystem.NoorThemeTokens
import com.poswal.noor.data.preferences.UserPreferencesRepository
import com.poswal.noor.ui.tasbih.DhikrPreset
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasbihScreen(
    preferencesRepository: UserPreferencesRepository,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = NoorThemeTokens.colors
    val typography = NoorThemeTokens.typography
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()

    val userPrefs by preferencesRepository.userPreferencesFlow.collectAsState(initial = null)
    val lifetimeCount = userPrefs?.tasbihLifetime ?: 0

    val presets = remember {
        listOf(
            DhikrPreset("سُبْحَانَ اللَّهِ", "SubhanAllah", "Glory be to Allah", 33),
            DhikrPreset("الْحَمْدُ لِلَّهِ", "Alhamdulillah", "Praise be to Allah", 33),
            DhikrPreset("اللَّهُ أَكْبَرُ", "Allahu Akbar", "Allah is the Greatest", 34),
            DhikrPreset("أَسْتَغْفِرُ اللَّهَ", "Astaghfirullah", "I seek forgiveness from Allah", 100),
            DhikrPreset("لَا إِلَٰهَ إِلَّا اللَّهُ", "La ilaha illallah", "None worthy of worship but Allah", 100),
            DhikrPreset("سُبْحَانَ اللَّهِ وَبِحَمْدِهِ", "SubhanAllahi wa bihamdihi", "Glory and praise to Allah", 100)
        )
    }

    var selectedPreset by remember { mutableStateOf(presets.first()) }
    var currentCount by remember { mutableIntStateOf(0) }
    var roundsCompleted by remember { mutableIntStateOf(0) }

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1.0f,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = 400f),
        label = "tasbih_press_scale"
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = colors.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Digital Tasbih",
                            style = typography.title.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                            color = colors.textPrimary
                        )
                        Text(
                            text = "Lifetime: $lifetimeCount counts",
                            style = typography.caption.copy(fontSize = 11.sp),
                            color = colors.textSecondary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = colors.textPrimary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        currentCount = 0
                        roundsCompleted = 0
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset Current Count",
                            tint = colors.textSecondary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colors.background)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Presets Horizontal Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = NoorSpacing.l, vertical = NoorSpacing.m),
                horizontalArrangement = Arrangement.spacedBy(NoorSpacing.s)
            ) {
                presets.forEach { preset ->
                    val isSelected = selectedPreset == preset
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) colors.primary else colors.surface,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) colors.primary else colors.outline
                        ),
                        modifier = Modifier
                            .clickable {
                                selectedPreset = preset
                                currentCount = 0
                                roundsCompleted = 0
                            }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = preset.titleEn,
                                style = typography.caption.copy(
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                ),
                                color = if (isSelected) colors.onPrimary else colors.textPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${preset.target}",
                                style = typography.caption.copy(
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = if (isSelected) colors.onPrimary.copy(alpha = 0.8f) else colors.primary
                            )
                        }
                    }
                }
            }

            // Dhikr Info Header
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = NoorSpacing.xl)
            ) {
                Text(
                    text = selectedPreset.titleAr,
                    style = typography.arabicQuran.copy(fontSize = 32.sp, lineHeight = 50.sp),
                    color = colors.textPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(NoorSpacing.xs))

                Text(
                    text = selectedPreset.titleEn,
                    style = typography.title.copy(fontSize = 18.sp, fontWeight = FontWeight.SemiBold),
                    color = colors.primary
                )

                Text(
                    text = selectedPreset.translation,
                    style = typography.caption.copy(fontSize = 13.sp),
                    color = colors.textSecondary,
                    textAlign = TextAlign.Center
                )
            }

            // Main Interactive Dial
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
                    .clip(CircleShape)
                    .background(colors.surfaceElevated)
                    .border(
                        width = 3.dp,
                        color = colors.primary.copy(alpha = 0.2f),
                        shape = CircleShape
                    )
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) {
                        val nextCount = currentCount + 1
                        val reachedTarget = nextCount >= selectedPreset.target

                        if (reachedTarget) {
                            currentCount = 0
                            roundsCompleted += 1
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        } else {
                            currentCount = nextCount
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        }

                        scope.launch {
                            preferencesRepository.incrementTasbihLifetime(1)
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                // Circular Progress Ring
                val progress = if (selectedPreset.target > 0) {
                    currentCount.toFloat() / selectedPreset.target.toFloat()
                } else 0f

                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxSize(),
                    color = colors.primary,
                    trackColor = colors.outline.copy(alpha = 0.3f),
                    strokeWidth = 6.dp
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$currentCount",
                        style = typography.display.copy(fontSize = 56.sp, fontWeight = FontWeight.Bold),
                        color = colors.textPrimary
                    )

                    Text(
                        text = "TARGET: ${selectedPreset.target}",
                        style = typography.caption.copy(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = colors.primary
                    )

                    if (roundsCompleted > 0) {
                        Spacer(modifier = Modifier.height(NoorSpacing.xs))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = colors.accent.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "Round $roundsCompleted completed",
                                style = typography.caption.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                                color = colors.accent,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            // Bottom Prompt
            Text(
                text = "Tap anywhere on the circle to count",
                style = typography.caption.copy(fontSize = 13.sp),
                color = colors.textSecondary,
                modifier = Modifier.padding(bottom = NoorSpacing.xxl)
            )
        }
    }
}
