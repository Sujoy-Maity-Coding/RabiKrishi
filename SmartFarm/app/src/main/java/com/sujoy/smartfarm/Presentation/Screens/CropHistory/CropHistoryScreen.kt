package com.sujoy.smartfarm.Presentation.Screens.CropHistory

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.sujoy.smartfarm.Domain.model.DailyFarmUpdate
import com.sujoy.smartfarm.Presentation.Components.Dashboard.FarmTopBar
import com.sujoy.smartfarm.Presentation.Components.FarmList.SectionCard
import com.sujoy.smartfarm.Presentation.ViewModel.AppViewModel
import com.sujoy.smartfarm.ui.theme.GreenContainer
import com.sujoy.smartfarm.ui.theme.GreenPrimary
import com.sujoy.smartfarm.ui.theme.OffWhite
import com.sujoy.smartfarm.ui.theme.OutlineGreen
import com.sujoy.smartfarm.ui.theme.TextPrimary
import com.sujoy.smartfarm.ui.theme.TextSecondary
import com.sujoy.smartfarm.ui.theme.WhitePure

private fun riskColor(risk: String?): Color = when (risk?.lowercase()) {
    "healthy", "low"        -> Color(0xFF2E7D32)
    "medium", "medium risk" -> Color(0xFFF57F17)
    "high", "high risk"     -> Color(0xFFE65100)
    "critical"               -> Color(0xFFC62828)
    else                     -> TextSecondary
}

private fun riskEmoji(risk: String?): String = when (risk?.lowercase()) {
    "healthy", "low"        -> "✅"
    "medium", "medium risk" -> "⚠️"
    "high", "high risk"     -> "🔶"
    "critical"               -> "🚨"
    else                     -> "❔"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CropHistoryScreen(

    farmId: String,

    navController: NavHostController,

    appViewModel: AppViewModel = hiltViewModel()

) {

    val state by appViewModel.cropHistoryState.collectAsState()

    LaunchedEffect(Unit) {
        appViewModel.getCropHistory(farmId)
    }

    var selectedUpdate by remember { mutableStateOf<DailyFarmUpdate?>(null) }

    Scaffold(
        containerColor = OffWhite,
        topBar = {
            FarmTopBar(
                title = "Crop Health History",
                showBack = true,
                onBack = { navController.popBackStack() }
            )
        }
    ) { padding ->

        when {

            // ── Loading
            state.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(GreenContainer),
                            contentAlignment = Alignment.Center
                        ) { Text("📈", fontSize = 32.sp) }
                        CircularProgressIndicator(color = GreenPrimary, strokeWidth = 3.dp)
                        Text("Loading history…", fontSize = 13.sp, color = TextSecondary)
                    }
                }
            }

            // ── Error
            state.error.isNotEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("⚠️", fontSize = 40.sp)
                        Text(
                            "Failed to load",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(state.error, fontSize = 12.sp, color = TextSecondary)
                    }
                }
            }

            // ── Empty
            state.updates.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("🌱", fontSize = 40.sp)
                        Text(
                            "No updates yet",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            "Analyze your crop to start building a health history",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }
            }

            // ── Content
            else -> {
                val scores = state.updates.sortedBy { it.day }.map { it.aiResult.healthScore }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(IntrinsicSize.Max),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            val avgScore = if (scores.isNotEmpty()) scores.average().toInt() else 0
                            val maxScore = scores.maxOrNull() ?: 0
                            val minScore = scores.minOrNull() ?: 0

                            StatSummaryTile("📊", "Average", "$avgScore", Modifier.weight(1f))
                            StatSummaryTile("📈", "Best", "$maxScore", Modifier.weight(1f))
                            StatSummaryTile("📉", "Lowest", "$minScore", Modifier.weight(1f))
                        }
                    }

                    item {
                        SectionCard {
                            HealthTrendChart(scores = scores)
                        }
                    }

                    item {
                        Text(
                            "Update Log",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                    items(state.updates) { update ->
                        val ai = update.aiResult
                        val accent = riskColor(ai.riskLevel)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(18.dp))
                                .background(WhitePure)
                                .border(1.dp, OutlineGreen, RoundedCornerShape(18.dp))
                                .clickable { selectedUpdate = update }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            // Day badge
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(accent.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    riskEmoji(ai.riskLevel),
                                    fontSize = 20.sp
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "Day ${update.day}",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                                Text(
                                    ai.diseaseName.ifBlank { "Healthy" },
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Spacer(Modifier.height(4.dp))
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(GreenContainer)
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text(
                                            "💚 ${ai.healthScore}",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = GreenPrimary
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(accent.copy(alpha = 0.12f))
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text(
                                            ai.riskLevel.ifBlank { "--" },
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = accent
                                        )
                                    }
                                }
                            }

                            Icon(
                                Icons.Outlined.ChevronRight,
                                contentDescription = null,
                                tint = OutlineGreen
                            )
                        }
                        Spacer(Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { (ai.healthScore / 100f).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = accent,
                            trackColor = accent.copy(alpha = 0.12f),
                            strokeCap = StrokeCap.Round
                        )
                    }

                    item { Spacer(Modifier.height(10.dp)) }
                }
            }
        }
    }

    selectedUpdate?.let { update ->
        ModalBottomSheet(
            onDismissRequest = { selectedUpdate = null },
            containerColor = WhitePure
        ) {
            CropHealthReport(update = update)
        }
    }
}

@Composable
private fun StatSummaryTile(
    emoji: String,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(14.dp))
            .background(WhitePure)
            .border(1.dp, OutlineGreen, RoundedCornerShape(14.dp))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(emoji, fontSize = 18.sp)
        Text(value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = GreenPrimary)
        Text(label, fontSize = 10.sp, color = TextSecondary)
    }
}
