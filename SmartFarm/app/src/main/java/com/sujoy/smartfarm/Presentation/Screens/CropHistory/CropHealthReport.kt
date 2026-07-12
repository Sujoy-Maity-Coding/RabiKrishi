package com.sujoy.smartfarm.Presentation.Screens.CropHistory

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.sujoy.smartfarm.Domain.model.DailyFarmUpdate
import com.sujoy.smartfarm.ui.theme.GreenContainer
import com.sujoy.smartfarm.ui.theme.GreenOnContainer
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

@Composable
fun CropHealthReport(
    update: DailyFarmUpdate
) {
    val ai = update.aiResult

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // ── Handle bar
        item {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(OutlineGreen)
                )
            }
        }

        // ── Image
        if (update.imageUrl.isNotBlank()) {
            item {
                AsyncImage(
                    model = update.imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(18.dp))
                )
            }
        }

        // ── Header: disease + day pill
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Day ${update.day}",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                    Text(
                        ai.diseaseName.ifBlank { "🌾 Healthy" },
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(riskColor(ai.riskLevel).copy(alpha = 0.12f))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        "${riskEmoji(ai.riskLevel)} ${ai.riskLevel.ifBlank { "--" }}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = riskColor(ai.riskLevel)
                    )
                }
            }
        }

        // ── Metric tiles
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(OffWhite)
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("💚", fontSize = 14.sp)
                        Text("Health score", fontSize = 11.sp, color = TextSecondary)
                    }
                    Text(
                        "${ai.healthScore}/100",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = riskColor(ai.riskLevel)
                    )
                }
                LinearProgressIndicator(
                    progress = { (ai.healthScore / 100f).coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = riskColor(ai.riskLevel),
                    trackColor = riskColor(ai.riskLevel).copy(alpha = 0.12f),
                    strokeCap = StrokeCap.Round
                )
            }
        }
        if (ai.severity.isNotBlank() || ai.currentPhase.isNotBlank()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (ai.severity.isNotBlank()) {
                        ReportTile("📊", "Severity", ai.severity, Modifier.weight(1f))
                    }
                    if (ai.currentPhase.isNotBlank()) {
                        ReportTile("📍", "Phase", ai.currentPhase, Modifier.weight(1f))
                    }
                }
            }
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(OutlineGreen.copy(alpha = 0.4f))
            )
        }

        // ── Recommendations
        item { SectionHeading("💊 Recommendations") }
        item { ReportRow("💊", "Recommended Medicine", ai.recommendedMedicine) }
        item { ReportRow("📦", "Medicine Quantity", ai.medicineQuantity) }
        item { ReportRow("🌿", "Organic Treatment", ai.organicTreatment) }
        item { ReportRow("🧪", "Chemical Treatment", ai.chemicalTreatment) }
        item { ReportRow("💧", "Irrigation Advice", ai.irrigationAdvice) }
        item { ReportRow("🌱", "Fertilizer Advice", ai.fertilizerAdvice) }

        // ── Today's tasks
        if (ai.todayTasks.isNotEmpty()) {
            item { SectionHeading("✅ Today's Tasks") }
            items(ai.todayTasks) { task ->
                ChecklistRow(task)
            }
        }

        // ── Preventive tips
        if (ai.preventiveTips.isNotEmpty()) {
            item { SectionHeading("🛡 Preventive Tips") }
            items(ai.preventiveTips) { tip ->
                BulletRow(tip)
            }
        }

        item { Spacer(Modifier.height(12.dp)) }
    }
}

@Composable
private fun ReportTile(
    emoji: String,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(OffWhite)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(emoji, fontSize = 13.sp)
            Text(label, fontSize = 10.sp, color = TextSecondary)
        }
        Text(
            value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            maxLines = 2
        )
    }
}

@Composable
private fun SectionHeading(text: String) {
    Text(
        text,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = GreenPrimary
    )
}

@Composable
private fun ReportRow(emoji: String, label: String, value: String) {
    if (value.isBlank()) return
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(emoji, fontSize = 12.sp)
            Text(
                label,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondary
            )
        }
        Spacer(Modifier.height(3.dp))
        Text(
            value,
            fontSize = 13.sp,
            color = TextPrimary,
            lineHeight = 18.sp
        )
    }
}

@Composable
private fun ChecklistRow(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(GreenContainer)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("✔️", fontSize = 13.sp)
        Text(text, fontSize = 12.sp, color = GreenOnContainer)
    }
}

@Composable
private fun BulletRow(text: String) {
    Row(
        modifier = Modifier.padding(vertical = 3.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .padding(top = 6.dp)
                .size(6.dp)
                .clip(CircleShape)
                .background(GreenPrimary)
        )
        Text(
            text,
            fontSize = 12.sp,
            color = TextPrimary,
            lineHeight = 17.sp
        )
    }
}
