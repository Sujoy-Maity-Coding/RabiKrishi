package com.sujoy.smartfarm.Presentation.Components.Crop

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.sujoy.smartfarm.Domain.model.Crop
import com.sujoy.smartfarm.Presentation.Components.Crop.DistrictDropDown
import com.sujoy.smartfarm.Presentation.Components.Crop.MonthDropDown
import com.sujoy.smartfarm.Presentation.Components.Crop.SeasonDropDown
import com.sujoy.smartfarm.Presentation.Components.Crop.SoilDropDown
import com.sujoy.smartfarm.Presentation.Components.Dashboard.FeatureCard
import com.sujoy.smartfarm.Presentation.Navigation.FarmerRoutes
import com.sujoy.smartfarm.Presentation.Utils.CropRecommend.DistrictItem
import com.sujoy.smartfarm.Presentation.Utils.CropRecommend.SeasonItem
import com.sujoy.smartfarm.Presentation.Utils.CropRecommend.SoilItem
import com.sujoy.smartfarm.Presentation.Utils.CropRecommend.localizedDigits
import com.sujoy.smartfarm.Presentation.Utils.CropRecommend.translatedDistrictHint
import com.sujoy.smartfarm.Presentation.Utils.CropRecommend.translatedDistrictName
import com.sujoy.smartfarm.Presentation.Utils.CropRecommend.translatedSeasonMonths
import com.sujoy.smartfarm.Presentation.Utils.CropRecommend.translatedSeasonName
import com.sujoy.smartfarm.Presentation.Utils.CropRecommend.translatedSoilName
import com.sujoy.smartfarm.Presentation.Utils.CropRecommend.translatedSoilTrait
import com.sujoy.smartfarm.Presentation.ViewModel.AppViewModel
import com.sujoy.smartfarm.R
import com.sujoy.smartfarm.ui.theme.GreenContainer
import com.sujoy.smartfarm.ui.theme.GreenOnContainer
import com.sujoy.smartfarm.ui.theme.GreenPrimary
import com.sujoy.smartfarm.ui.theme.OffWhite
import com.sujoy.smartfarm.ui.theme.OutlineGreen
import com.sujoy.smartfarm.ui.theme.TextPrimary
import com.sujoy.smartfarm.ui.theme.TextSecondary
import com.sujoy.smartfarm.ui.theme.WhitePure

@Composable
fun DistrictPicker(
    items: List<DistrictItem>,
    selected: String,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val chevronRotation by animateFloatAsState(if (expanded) 180f else 0f, label = "chevron")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(WhitePure)
            .border(
                1.dp,
                if (selected.isNotEmpty()) GreenPrimary else OutlineGreen,
                RoundedCornerShape(16.dp)
            )
    ) {
        // Trigger row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (selected.isEmpty()) {
                Text(stringResource(R.string.select_district_placeholder), color = TextSecondary, fontSize = 14.sp)
            } else {
                val item = items.find { it.name == selected }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(item?.emoji ?: "📍", fontSize = 20.sp)
                    Column {
                        Text(translatedDistrictName(selected), fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                        Text(translatedDistrictHint(selected), fontSize = 11.sp, color = TextSecondary)
                    }
                }
            }
            Icon(
                Icons.Outlined.KeyboardArrowDown,
                contentDescription = null,
                tint = GreenPrimary,
                modifier = Modifier
                    .size(20.dp)
                    .rotate(chevronRotation)
            )
        }

        // Expanded list
        AnimatedVisibility(visible = expanded) {
            Column {
                Divider(color = GreenContainer, thickness = 1.dp)
                items.forEach { item ->
                    val isSelected = item.name == selected
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSelect(item.name) // still English — unchanged, used by Gemini
                                expanded = false
                            }
                            .background(if (isSelected) GreenContainer else Color.Transparent)
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(item.emoji, fontSize = 22.sp)
                            Column {
                                Text(translatedDistrictName(item.name), fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                                Text(translatedDistrictHint(item.name), fontSize = 11.sp, color = TextSecondary)
                            }
                        }
                        if (isSelected) {
                            Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
    }
}

// ── Month picker (horizontal scroll chips) ────────────────────────────────────

@Composable
fun MonthPicker(selectedMonth: Int, onSelect: (Int) -> Unit) {
    val monthShort = stringArrayResource(R.array.month_short_names)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        (1..12).forEach { m ->
            val isSelected = m == selectedMonth
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSelected) GreenPrimary else WhitePure)
                    .border(
                        1.dp,
                        if (isSelected) GreenPrimary else OutlineGreen,
                        RoundedCornerShape(12.dp)
                    )
                    .clickable { onSelect(m) }
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    monthShort[m - 1],
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isSelected) WhitePure else TextPrimary
                )
                Text(
                    localizedDigits(text = "$m"),
                    fontSize = 10.sp,
                    color = if (isSelected) WhitePure.copy(alpha = 0.75f) else TextSecondary
                )
            }
        }
    }
}

// ── Season picker (3 big cards) ───────────────────────────────────────────────

@Composable
fun SeasonPicker(
    items: List<SeasonItem>,
    selected: String,
    onSelect: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items.forEach { item ->
            val isSelected = item.name == selected
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isSelected) GreenPrimary else WhitePure)
                    .border(
                        1.5.dp,
                        if (isSelected) GreenPrimary else OutlineGreen,
                        RoundedCornerShape(14.dp)
                    )
                    .clickable { onSelect(item.name) }
                    .padding(vertical = 14.dp, horizontal = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(item.emoji, fontSize = 26.sp)
                Text(
                    translatedSeasonName(item.name),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isSelected) WhitePure else TextPrimary
                )
                Text(
                    translatedSeasonMonths(item.name),
                    fontSize = 10.sp,
                    color = if (isSelected) WhitePure.copy(alpha = 0.75f) else TextSecondary
                )
            }
        }
    }
}

// ── Soil picker (2x2 grid) ────────────────────────────────────────────────────

@Composable
fun SoilPicker(
    items: List<SoilItem>,
    selected: String,
    onSelect: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        items.chunked(2).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                row.forEach { item ->
                    val isSelected = item.name == selected
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isSelected) GreenContainer else WhitePure)
                            .border(
                                if (isSelected) 2.dp else 1.dp,
                                if (isSelected) GreenPrimary else OutlineGreen,
                                RoundedCornerShape(14.dp)
                            )
                            .clickable { onSelect(item.name) }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) GreenPrimary else OffWhite),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(item.emoji, fontSize = 18.sp)
                        }
                        Column {
                            Text(
                                translatedSoilName(item.name),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isSelected) GreenOnContainer else TextPrimary
                            )
                            Text(
                                translatedSoilTrait(item.name),
                                fontSize = 10.sp,
                                color = TextSecondary,
                                lineHeight = 13.sp
                            )
                        }
                    }
                }
                // fill odd row
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}