package com.sujoy.smartfarm.Presentation.Screens.FarmList

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Agriculture
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.SquareFoot
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.sujoy.smartfarm.Domain.model.Farm
import com.sujoy.smartfarm.Presentation.Navigation.FarmerRoutes
import com.sujoy.smartfarm.Presentation.Utils.CropRecommend.translatedCropName
import com.sujoy.smartfarm.Presentation.Utils.FarmMethod.translatedLandArea
import com.sujoy.smartfarm.Presentation.Utils.FarmMethod.translatedMethodLabelFromString
import com.sujoy.smartfarm.Presentation.ViewModel.AppViewModel
import com.sujoy.smartfarm.R
import com.sujoy.smartfarm.ui.theme.GreenContainer
import com.sujoy.smartfarm.ui.theme.GreenPrimary
import com.sujoy.smartfarm.ui.theme.OffWhite
import com.sujoy.smartfarm.ui.theme.OutlineGreen
import com.sujoy.smartfarm.ui.theme.TextPrimary
import com.sujoy.smartfarm.ui.theme.TextSecondary
import com.sujoy.smartfarm.ui.theme.WhitePure
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ── Helpers ───────────────────────────────────────────────────────────────────

fun Long.toDateString(): String =
    SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(this))

private fun methodColor(method: String): Color = when (method.lowercase()) {
    "organic"   -> Color(0xFF2E7D32)
    "inorganic" -> Color(0xFF1565C0)
    else        -> Color(0xFFF57F17)
}

private fun methodEmoji(method: String): String = when (method.lowercase()) {
    "organic"   -> "🌿"
    "inorganic" -> "🧪"
    else        -> "⚗️"
}

private fun cropEmoji(cropName: String): String = when {
    cropName.contains("rice",        ignoreCase = true) -> "🌾"
    cropName.contains("jute",        ignoreCase = true) -> "🪢"
    cropName.contains("mustard",     ignoreCase = true) -> "🌻"
    cropName.contains("potato",      ignoreCase = true) -> "🥔"
    cropName.contains("wheat",       ignoreCase = true) -> "🌽"
    cropName.contains("maize",       ignoreCase = true) -> "🌽"
    cropName.contains("tomato",      ignoreCase = true) -> "🍅"
    cropName.contains("brinjal",     ignoreCase = true) -> "🍆"
    cropName.contains("cabbage",     ignoreCase = true) -> "🥬"
    cropName.contains("cauliflower", ignoreCase = true) -> "🥦"
    cropName.contains("gram",        ignoreCase = true) -> "🫘"
    else -> "🌱"
}

// ── FarmCard ──────────────────────────────────────────────────────────────────

@Composable
fun FarmCard(
    farm: Farm,
    onOpenFarm: () -> Unit
) {
    val accent = methodColor(farm.farmingMethod)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = WhitePure),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, OutlineGreen)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // ── Top row: crop emoji + name + method badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Crop emoji circle
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(GreenContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(cropEmoji(farm.cropName), fontSize = 22.sp)
                    }
                    Column {
                        Text(
                            translatedCropName(farm.cropName),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            farm.farmName,
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }

                // Method badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(accent.copy(alpha = 0.12f))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        "${methodEmoji(farm.farmingMethod)} ${translatedMethodLabelFromString(farm.farmingMethod)}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = accent
                    )
                }
            }

            Spacer(Modifier.height(14.dp))
            HorizontalDivider(color = GreenContainer, thickness = 1.dp)
            Spacer(Modifier.height(12.dp))

            // ── Info grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FarmInfoChip(
                    icon = Icons.Outlined.SquareFoot,
                    label = stringResource(R.string.area_label),
                    value = translatedLandArea(farm.landArea),
                    modifier = Modifier.weight(1f)
                )
                FarmInfoChip(
                    icon = Icons.Outlined.CalendarToday,
                    label = stringResource(R.string.started_label),
                    value = farm.startDate.toDateString(),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(10.dp))

            // Status chip
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(
                            if (farm.isCompleted) TextSecondary else Color(0xFF43A047)
                        )
                )
                Text(
                    text = if (farm.isCompleted) stringResource(R.string.status_completed) else stringResource(R.string.status_active),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (farm.isCompleted) TextSecondary else Color(0xFF2E7D32)
                )
            }

            Spacer(Modifier.height(12.dp))

            // ── Open Farm button
            Button(
                onClick = onOpenFarm,
                modifier = Modifier.fillMaxWidth().height(46.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = accent,
                    contentColor   = WhitePure
                )
            ) {
                androidx.compose.material3.Icon(
                    Icons.Outlined.Agriculture,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    stringResource(R.string.open_farm_btn),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

// ── Info chip ─────────────────────────────────────────────────────────────────

@Composable
private fun FarmInfoChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(OffWhite)
            .padding(horizontal = 10.dp, vertical = 9.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            androidx.compose.material3.Icon(icon, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(12.dp))
            Text(label, fontSize = 10.sp, color = TextSecondary)
        }
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
    }
}