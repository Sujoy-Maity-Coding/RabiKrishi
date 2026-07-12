package com.sujoy.smartfarm.Presentation.Screens.CropRecommend

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Landscape
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.sujoy.smartfarm.Presentation.Components.Crop.DistrictPicker
import com.sujoy.smartfarm.Presentation.Components.Crop.MonthPicker
import com.sujoy.smartfarm.Presentation.Components.Crop.SeasonPicker
import com.sujoy.smartfarm.Presentation.Components.Crop.SectionLabel
import com.sujoy.smartfarm.Presentation.Components.Crop.SoilPicker
import com.sujoy.smartfarm.Presentation.Components.Crop.StepRow
import com.sujoy.smartfarm.Presentation.Components.Dashboard.FarmTopBar
import com.sujoy.smartfarm.Presentation.Components.Dashboard.PrimaryButton
import com.sujoy.smartfarm.Presentation.Navigation.FarmerRoutes
import com.sujoy.smartfarm.Presentation.Utils.CropRecommend.districtItems
import com.sujoy.smartfarm.Presentation.Utils.CropRecommend.seasonItems
import com.sujoy.smartfarm.Presentation.Utils.CropRecommend.soilItems
import com.sujoy.smartfarm.ui.theme.GreenPrimary
import com.sujoy.smartfarm.ui.theme.OffWhite
import com.sujoy.smartfarm.ui.theme.WhitePure

@Preview(showSystemUi = true)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Demo() {

    var district by remember { mutableStateOf("") }
    var season   by remember { mutableStateOf("") }
    var soilType by remember { mutableStateOf("") }
    var month    by remember { mutableIntStateOf(1) }

    val allFilled = district.isNotEmpty() && season.isNotEmpty() && soilType.isNotEmpty()

    Scaffold(
        containerColor = OffWhite,
        topBar = {
            FarmTopBar(
                title = "Crop recommendation",
                showBack = true,
                onBack = {  }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Spacer(Modifier.height(4.dp))

            //Hero instruction card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(GreenPrimary)
                    .padding(20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(WhitePure.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🌿", fontSize = 24.sp)
                    }
                    Column {
                        Text(
                            "Tell us about your farm",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = WhitePure
                        )
                        Text(
                            "4 quick details → personalised crop list",
                            fontSize = 11.sp,
                            color = WhitePure.copy(alpha = 0.75f),
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }

            // ── Step indicator
            StepRow(
                steps = listOf("District", "Month", "Season", "Soil"),
                completed = listOf(
                    district.isNotEmpty(),
                    true,
                    season.isNotEmpty(),
                    soilType.isNotEmpty()
                )
            )

            // ── District picker
            SectionLabel(icon = Icons.Outlined.LocationOn, label = "Your district")
            DistrictPicker(
                items = districtItems,
                selected = district,
                onSelect = { district = it }
            )

            // ── Month picker
            SectionLabel(icon = Icons.Outlined.DateRange, label = "Planting month")
            MonthPicker(selectedMonth = month, onSelect = { month = it })

            // ── Season picker
            SectionLabel(icon = Icons.Outlined.WbSunny, label = "Crop season")
            SeasonPicker(
                items = seasonItems,
                selected = season,
                onSelect = { season = it }
            )

            // ── Soil picker
            SectionLabel(icon = Icons.Outlined.Landscape, label = "Soil type")
            SoilPicker(
                items = soilItems,
                selected = soilType,
                onSelect = { soilType = it }
            )

            Spacer(Modifier.height(8.dp))

            PrimaryButton(
                text = if (allFilled) "Get my recommendations →" else "Complete all fields to continue",
                enabled = allFilled,
                onClick = {

                }
            )

            Spacer(Modifier.height(24.dp))
        }
    }
}