package com.poswal.noor.feature.more.subscreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import com.poswal.noor.util.HijriCalendarHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HijriCalendarSheet(
    onDismiss: () -> Unit
) {
    val colors = NoorThemeTokens.colors
    val typography = NoorThemeTokens.typography
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val hijriDate = remember { HijriCalendarHelper.getHijriDate() }
    val gregorianFormatted = remember {
        SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault()).format(Date())
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = colors.surface,
        shape = NoorShapes.sheet
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = NoorSpacing.xxl)
        ) {
            // Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = NoorSpacing.l)
            ) {
                Text(
                    text = "Offline Hijri Calendar",
                    style = typography.title.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                    color = colors.textPrimary
                )
                Text(
                    text = "Algorithmic lunar calculation · Zero internet required",
                    style = typography.caption.copy(fontSize = 12.sp),
                    color = colors.textSecondary
                )
            }

            Spacer(modifier = Modifier.height(NoorSpacing.m))

            // Today's Hijri Banner
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = NoorSpacing.l),
                shape = NoorShapes.card,
                color = colors.primaryContainer.copy(alpha = 0.5f),
                border = androidx.compose.foundation.BorderStroke(1.dp, colors.primary.copy(alpha = 0.3f))
            ) {
                Column(
                    modifier = Modifier.padding(NoorSpacing.l),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "اليوم",
                        style = typography.caption.copy(fontSize = 12.sp, fontWeight = FontWeight.Bold),
                        color = colors.primary
                    )

                    Spacer(modifier = Modifier.height(NoorSpacing.xs))

                    Text(
                        text = "${hijriDate.day} ${hijriDate.monthNameAr} ${hijriDate.year} هـ",
                        style = typography.arabicQuran.copy(fontSize = 24.sp, lineHeight = 38.sp),
                        color = colors.textPrimary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(NoorSpacing.xs))

                    Text(
                        text = hijriDate.formattedDate,
                        style = typography.subtitle.copy(fontSize = 15.sp, fontWeight = FontWeight.SemiBold),
                        color = colors.primary
                    )

                    Text(
                        text = gregorianFormatted,
                        style = typography.caption.copy(fontSize = 12.sp),
                        color = colors.textSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(NoorSpacing.l))

            // Islamic Occasions List
            Text(
                text = "Key Islamic Occasions",
                style = typography.caption.copy(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                ),
                color = colors.textPrimary,
                modifier = Modifier.padding(horizontal = NoorSpacing.l)
            )

            Spacer(modifier = Modifier.height(NoorSpacing.s))

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(NoorSpacing.s)
            ) {
                items(HijriCalendarHelper.ISLAMIC_OCCASIONS) { occasion ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = NoorSpacing.l, vertical = NoorSpacing.xs),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(36.dp),
                            shape = CircleShape,
                            color = colors.surfaceElevated
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Event,
                                    contentDescription = null,
                                    tint = colors.accent,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(NoorSpacing.m))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = occasion.title,
                                style = typography.body.copy(fontSize = 14.sp, fontWeight = FontWeight.SemiBold),
                                color = colors.textPrimary
                            )
                            Text(
                                text = occasion.description,
                                style = typography.caption.copy(fontSize = 12.sp),
                                color = colors.textSecondary
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = colors.surfaceElevated
                        ) {
                            Text(
                                text = occasion.hijriDate,
                                style = typography.caption.copy(
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = colors.primary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
