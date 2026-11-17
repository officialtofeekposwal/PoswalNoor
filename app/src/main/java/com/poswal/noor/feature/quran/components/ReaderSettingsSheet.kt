package com.poswal.noor.feature.quran.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.poswal.noor.core.designsystem.NoorShapes
import com.poswal.noor.core.designsystem.NoorSpacing
import com.poswal.noor.core.designsystem.NoorThemeTokens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderSettingsSheet(
    arabicFontSize: Float,
    translationFontSize: Float,
    showTranslation: Boolean,
    lineSpacingMultiplier: Float,
    onArabicFontSizeChange: (Float) -> Unit,
    onTranslationFontSizeChange: (Float) -> Unit,
    onToggleShowTranslation: (Boolean) -> Unit,
    onLineSpacingMultiplierChange: (Float) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val colors = NoorThemeTokens.colors
    val typography = NoorThemeTokens.typography

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = colors.surfaceElevated,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = NoorSpacing.l)
                .padding(bottom = 36.dp)
        ) {
            Text(
                text = "Reader Settings",
                style = typography.title.copy(fontWeight = FontWeight.Bold),
                color = colors.textPrimary
            )
            Text(
                text = "Customize scripture typography & translation",
                style = typography.caption,
                color = colors.textSecondary
            )

            Spacer(modifier = Modifier.height(NoorSpacing.l))

            // 1. Live Preview Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(NoorShapes.card)
                    .background(colors.surface)
                    .padding(NoorSpacing.m)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ",
                        style = typography.arabicQuran.copy(
                            fontSize = arabicFontSize.sp,
                            lineHeight = (arabicFontSize * lineSpacingMultiplier).sp,
                            textAlign = TextAlign.Right
                        ),
                        color = colors.textPrimary,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (showTranslation) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "[All] praise is [due] to Allah, Lord of the worlds.",
                            style = typography.body.copy(
                                fontSize = translationFontSize.sp,
                                lineHeight = (translationFontSize * 1.5f).sp
                            ),
                            color = colors.textSecondary,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(NoorSpacing.l))

            // 2. Arabic Font Size Slider (22sp - 40sp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Arabic Font Size",
                    style = typography.body.copy(fontWeight = FontWeight.SemiBold),
                    color = colors.textPrimary
                )
                Text(
                    text = "${arabicFontSize.toInt()} sp",
                    style = typography.caption.copy(fontWeight = FontWeight.Bold),
                    color = colors.primary
                )
            }
            Slider(
                value = arabicFontSize,
                onValueChange = onArabicFontSizeChange,
                valueRange = 22f..40f,
                steps = 8,
                colors = SliderDefaults.colors(
                    thumbColor = colors.primary,
                    activeTrackColor = colors.primary,
                    inactiveTrackColor = colors.outline
                )
            )

            Spacer(modifier = Modifier.height(NoorSpacing.m))

            // 3. Translation Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Show Translation",
                        style = typography.body.copy(fontWeight = FontWeight.SemiBold),
                        color = colors.textPrimary
                    )
                    Text(
                        text = "Display localized meaning below each ayah",
                        style = typography.caption,
                        color = colors.textSecondary
                    )
                }
                Switch(
                    checked = showTranslation,
                    onCheckedChange = onToggleShowTranslation,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = colors.onPrimary,
                        checkedTrackColor = colors.primary
                    )
                )
            }

            if (showTranslation) {
                Spacer(modifier = Modifier.height(NoorSpacing.m))

                // 4. Translation Font Size Slider (13sp - 22sp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Translation Size",
                        style = typography.body.copy(fontWeight = FontWeight.SemiBold),
                        color = colors.textPrimary
                    )
                    Text(
                        text = "${translationFontSize.toInt()} sp",
                        style = typography.caption.copy(fontWeight = FontWeight.Bold),
                        color = colors.primary
                    )
                }
                Slider(
                    value = translationFontSize,
                    onValueChange = onTranslationFontSizeChange,
                    valueRange = 13f..22f,
                    steps = 8,
                    colors = SliderDefaults.colors(
                        thumbColor = colors.primary,
                        activeTrackColor = colors.primary,
                        inactiveTrackColor = colors.outline
                    )
                )
            }

            Spacer(modifier = Modifier.height(NoorSpacing.m))

            // 5. Line Spacing Multiplier (1.8x, 2.0x, 2.4x)
            Text(
                text = "Arabic Line Spacing",
                style = typography.body.copy(fontWeight = FontWeight.SemiBold),
                color = colors.textPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(1.8f to "Compact (1.8x)", 2.0f to "Standard (2.0x)", 2.4f to "Spacious (2.4x)").forEach { (multiplier, label) ->
                    val isSelected = (lineSpacingMultiplier == multiplier)
                    FilterChip(
                        selected = isSelected,
                        onClick = { onLineSpacingMultiplierChange(multiplier) },
                        label = { Text(label, style = typography.caption) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = colors.primaryContainer,
                            selectedLabelColor = colors.primary
                        )
                    )
                }
            }
        }
    }
}
