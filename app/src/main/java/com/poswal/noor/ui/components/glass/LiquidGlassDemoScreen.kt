package com.poswal.noor.ui.components.glass

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

/**
 * Demo screen that showcases the Apple-inspired Liquid Glass UI components
 * over an animated, colorful ambient gradient mesh background.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiquidGlassDemoScreen(
    onNavigateBack: () -> Unit = {}
) {
    val isDark = isSystemInDarkTheme()
    var selectedTab by remember { mutableStateOf("home") }
    var showSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    val navItems = remember {
        listOf(
            GlassNavItem("home", "Daily Noor", Icons.Rounded.Home),
            GlassNavItem("favorites", "Saved", Icons.Rounded.Favorite),
            GlassNavItem("settings", "Settings", Icons.Rounded.Settings)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isDark) Color(0xFF090D16) else Color(0xFFF1F5F9))
    ) {
        // --- Dynamic Animated Ambient Blobs (Gives glass its physical refraction context) ---
        AnimatedLiquidGlassBackground()

        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                LiquidGlassNavBar(
                    items = navItems,
                    selectedItemId = selectedTab,
                    onItemSelected = { selectedTab = it }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Glass Pill
                LiquidGlassSurface(
                    style = LiquidGlassDefaults.buttonStyle(cornerRadius = 24.dp),
                    modifier = Modifier.padding(bottom = 20.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.AutoAwesome,
                            contentDescription = null,
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Poswal Noor • Liquid Glass Engine",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color.White else Color(0xFF0F172A),
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                // Featured Liquid Glass Card
                LiquidGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    style = LiquidGlassDefaults.cardStyle(cornerRadius = 28.dp, elevation = 12.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "SURAH AL-ISRA • 17:23",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF10B981),
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Hinglish Translation",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Arabic Calligraphy
                        Text(
                            text = "وَقَضَىٰ رَبُّكَ أَلَّا تَعْبُدُوٓا۟ إِلَّآ إِيَّاهُ وَبِٱلْوَٰلِدَيْنِ إِحْسَٰنًا",
                            fontSize = 21.sp,
                            fontWeight = FontWeight.SemiBold,
                            lineHeight = 34.sp,
                            textAlign = TextAlign.Right,
                            color = if (isDark) Color.White else Color(0xFF0F172A),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Hinglish translation
                        Text(
                            text = "Aur aapke Rab ne faisla farma diya hai ke Tum Uske siwa kisi ki ibadat na karo aur walidain ke saath husn-e-sulook karo.",
                            fontSize = 14.sp,
                            lineHeight = 22.sp,
                            color = if (isDark) Color(0xFFE2E8F0) else Color(0xFF334155)
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Interactive Glass Actions inside Card
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            LiquidGlassButton(
                                onClick = { showSheet = true },
                                modifier = Modifier.weight(1f),
                                style = LiquidGlassDefaults.buttonStyle(cornerRadius = 16.dp)
                            ) {
                                Text(
                                    text = "Read Tafsir",
                                    color = if (isDark) Color.White else Color(0xFF0F172A)
                                )
                            }

                            LiquidGlassButton(
                                onClick = { /* Share */ },
                                modifier = Modifier.width(52.dp),
                                style = LiquidGlassDefaults.buttonStyle(cornerRadius = 16.dp),
                                contentPaddingHorizontal = 0.dp
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Share,
                                    contentDescription = "Share",
                                    tint = Color(0xFF10B981)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Secondary Glass Stats Card
                LiquidGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    style = LiquidGlassDefaults.cardStyle(cornerRadius = 24.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "30",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF10B981)
                            )
                            Text(
                                text = "Reflections",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "5",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF38BDF8)
                            )
                            Text(
                                text = "Languages",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "100%",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFF59E0B)
                            )
                            Text(
                                text = "Offline",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // Liquid Glass Bottom Sheet
        if (showSheet) {
            ModalBottomSheet(
                onDismissRequest = { showSheet = false },
                sheetState = sheetState,
                containerColor = Color.Transparent,
                scrimColor = Color.Black.copy(alpha = 0.45f)
            ) {
                LiquidGlassSurface(
                    style = LiquidGlassDefaults.sheetStyle(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 20.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Surah Al-Isra Tafsir Insight",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color.White else Color(0xFF0F172A)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Is ayat me Allah Ta'ala ne Tauheed ke foran baad walidain ke haq ka zikr farmaya hai, jo yeh sabit karta hai ke walidain ki khidmat aur unka adab ibadat ke baad sabse azeem amal hai.",
                            fontSize = 14.sp,
                            lineHeight = 22.sp,
                            textAlign = TextAlign.Center,
                            color = if (isDark) Color(0xFFCBD5E1) else Color(0xFF475569)
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        LiquidGlassButton(
                            onClick = {
                                scope.launch {
                                    sheetState.hide()
                                    showSheet = false
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Close", color = Color(0xFF10B981))
                        }
                    }
                }
            }
        }
    }
}

/**
 * Smooth ambient animated gradient background creating colorful light refraction behind glass surfaces.
 */
@Composable
fun AnimatedLiquidGlassBackground() {
    val infiniteTransition = rememberInfiniteTransition(label = "ambient_mesh")

    val offset1 by infiniteTransition.animateFloat(
        initialValue = -50f,
        targetValue = 60f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "blob1_anim"
    )

    val offset2 by infiniteTransition.animateFloat(
        initialValue = 70f,
        targetValue = -40f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "blob2_anim"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // Emerald / Teal Ambient Blob
        Box(
            modifier = Modifier
                .size(320.dp)
                .offset(x = (-40 + offset1).dp, y = (60 + offset2).dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF10B981).copy(alpha = 0.45f), Color.Transparent)
                    ),
                    shape = CircleShape
                )
                .blur(50.dp)
        )

        // Indigo / Blue Ambient Blob
        Box(
            modifier = Modifier
                .size(280.dp)
                .align(Alignment.TopEnd)
                .offset(x = (40 - offset2).dp, y = (180 + offset1).dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF0284C7).copy(alpha = 0.40f), Color.Transparent)
                    ),
                    shape = CircleShape
                )
                .blur(55.dp)
        )

        // Warm Amber / Sunset Glow Blob
        Box(
            modifier = Modifier
                .size(340.dp)
                .align(Alignment.BottomCenter)
                .offset(x = (offset1 * 0.7f).dp, y = (-80 - offset2).dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFFEAB308).copy(alpha = 0.32f), Color.Transparent)
                    ),
                    shape = CircleShape
                )
                .blur(65.dp)
        )
    }
}
