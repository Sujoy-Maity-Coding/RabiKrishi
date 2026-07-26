package com.sujoy.smartfarm.Presentation.Screens.FarmList

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material.icons.outlined.Yard
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.sujoy.smartfarm.Domain.model.Farm
import com.sujoy.smartfarm.Presentation.Components.Dashboard.FarmTopBar
import com.sujoy.smartfarm.Presentation.Components.FarmList.FarmInputField
import com.sujoy.smartfarm.Presentation.Components.FarmList.InfoChip
import com.sujoy.smartfarm.Presentation.Components.FarmList.SectionLabel
import com.sujoy.smartfarm.Presentation.Navigation.FarmerRoutes
import com.sujoy.smartfarm.Presentation.ViewModel.AppViewModel
import com.sujoy.smartfarm.ui.theme.OffWhite
import com.sujoy.smartfarm.ui.theme.OutlineGreen
import com.sujoy.smartfarm.ui.theme.TextPrimary
import com.sujoy.smartfarm.ui.theme.TextSecondary
import com.sujoy.smartfarm.ui.theme.WhitePure
import androidx.compose.ui.res.stringResource
import com.sujoy.smartfarm.R
import com.sujoy.smartfarm.Presentation.Utils.CropRecommend.translatedCropName
import com.sujoy.smartfarm.Presentation.Utils.CropRecommend.localizedDigits
import com.sujoy.smartfarm.Presentation.Utils.FarmMethod.translatedMethodLabelFromString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateFarmScreen(
    cropId: String,
    cropName: String,
    farmingMethod: String,
    farmSize: Double,
    district: String,     // ← new
    season: String,
    navController: NavHostController,
    appViewModel: AppViewModel = hiltViewModel(),
    estimatedCost: String,
    estimatedYield: String,
    estimatedDuration: String,
    labourRequired: String,
    notes: String
) {
    var farmName  by remember { mutableStateOf("") }

    val state     by appViewModel.createFarmState.collectAsState()
    val accent     = com.sujoy.smartfarm.Presentation.Components.FarmList.methodColor(farmingMethod)
    val currentLangCode = androidx.compose.ui.platform.LocalConfiguration.current.locales[0].language
    LaunchedEffect(state.success) {
        if (state.success.isNotEmpty()) {
            Toast.makeText(navController.context, state.success, Toast.LENGTH_SHORT).show()
            navController.navigate(FarmerRoutes.HomeScreen) {
                popUpTo(
                    FarmerRoutes.CreateFarmScreen(cropId, cropName, farmingMethod, farmSize, estimatedCost, estimatedYield, estimatedDuration, labourRequired, notes, district, season)
                ) { inclusive = true }
            }
        }
    }

    Scaffold(
        containerColor = OffWhite,
        topBar = {
            FarmTopBar(
                title = stringResource(R.string.create_farm_title),
                showBack = true,
                onBack = { navController.popBackStack() }
            )
        }
    ) { padding ->

        Box(modifier = Modifier
            .fillMaxSize()
            .padding(padding)) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(Modifier.height(4.dp))

                // ── Selected crop + method summary banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(accent)
                        .padding(18.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {

                        // Top row: emoji + method badge
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(WhitePure.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(com.sujoy.smartfarm.Presentation.Components.FarmList.methodEmoji(farmingMethod), fontSize = 24.sp)
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(WhitePure.copy(alpha = 0.2f))
                                    .padding(horizontal = 12.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    "${translatedMethodLabelFromString(farmingMethod)} ${stringResource(R.string.farming_suffix)}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = WhitePure
                                )
                            }
                        }

                        // Crop name
                        Text(
                            translatedCropName(cropName),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = WhitePure
                        )
                        Text(
                            stringResource(R.string.fill_details_subtitle),
                            fontSize = 11.sp,
                            color = WhitePure.copy(alpha = 0.75f)
                        )
                    }
                }

                // ── Section label
                SectionLabel(icon = Icons.Outlined.Eco, label = stringResource(R.string.farm_details_label))

                // ── Farm Name field
                FarmInputField(
                    value = farmName,
                    onValueChange = { farmName = it },
                    label = stringResource(R.string.farm_name_label),
                    placeholder = stringResource(R.string.farm_name_placeholder),
                    icon = Icons.Outlined.Yard,
                    accentColor = accent
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(WhitePure)
                        .border(1.dp, OutlineGreen, RoundedCornerShape(14.dp))
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(11.dp))
                            .background(accent.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("📏", fontSize = 20.sp)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(stringResource(id = R.string.farm_size_stat_label), fontSize = 11.sp, color = TextSecondary)
                        Text(
                            "${localizedDigits("$farmSize")} ${stringResource(R.string.acre_suffix)}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(accent.copy(alpha = 0.1f))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            stringResource(R.string.confirmed_label),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = accent
                        )
                    }
                }
                // ── Info chips row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    InfoChip(
                        icon = "📅",
                        label = stringResource(R.string.starts_today_label),
                        modifier = Modifier.weight(1f)
                    )
                    InfoChip(
                        icon = "📋",
                        label = stringResource(R.string.phase_plan_included_label),
                        modifier = Modifier.weight(1f)
                    )
                }

                // ── Note card
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(accent.copy(alpha = 0.08f))
                        .border(1.dp, accent.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text("💡", fontSize = 16.sp)
                    Text(
                        stringResource(R.string.farm_plan_note),
                        fontSize = 11.sp,
                        color = TextSecondary,
                        lineHeight = 16.sp
                    )
                }

                Spacer(Modifier.height(4.dp))

                // ── Start Farming button
                Button(
                    onClick = {
                        if (farmName.isBlank()) return@Button
                        val farm = Farm(
                            farmName      = farmName,
                            cropId        = cropId,
                            cropName      = cropName,
                            farmingMethod = farmingMethod,
                            landArea = "$farmSize Acre",
                            startDate     = System.currentTimeMillis(),
                            isCompleted   = false,
                            aiEstimatedCost = estimatedCost,

                            aiEstimatedYield = estimatedYield,

                            aiEstimatedDuration = estimatedDuration,

                            aiLabourRequired = labourRequired,

                            aiNotes = notes,

                            aiEstimatedFarmSize = farmSize
                        )
                        appViewModel.createFarm(farm, district, season, currentLangCode)
                    },
                    enabled = farmName.isNotBlank() && !state.isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = accent,
                        contentColor   = WhitePure,
                        disabledContainerColor = OutlineGreen,
                        disabledContentColor   = WhitePure
                    )
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            color = WhitePure,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))   // ← new
                        Text(stringResource(R.string.preparing_farm_schedule))
                    } else {
                        Text(
                            stringResource(R.string.start_farming_btn),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Error message
                if (state.error.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFFFEBEE))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = state.error,
                            color = Color(0xFFB71C1C),
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}