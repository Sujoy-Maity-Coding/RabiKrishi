package com.sujoy.smartfarm.Presentation.Components.CropMethod

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Agriculture
import androidx.compose.material.icons.outlined.CurrencyRupee
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Landscape
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.sujoy.smartfarm.Presentation.Components.Crop.DistrictPicker
import com.sujoy.smartfarm.Presentation.Components.Crop.MonthPicker
import com.sujoy.smartfarm.Presentation.Components.Crop.SeasonPicker
import com.sujoy.smartfarm.Presentation.Components.Crop.SectionLabel
import com.sujoy.smartfarm.Presentation.Components.Crop.SoilPicker
import com.sujoy.smartfarm.Presentation.Components.Crop.StepRow
import com.sujoy.smartfarm.Presentation.Components.CropMethod.FarmMethod
import com.sujoy.smartfarm.Presentation.Components.CropMethod.MethodErrorView
import com.sujoy.smartfarm.Presentation.Components.CropMethod.MethodLoadingView
import com.sujoy.smartfarm.Presentation.Components.CropMethod.MetricCard
import com.sujoy.smartfarm.Presentation.Components.Dashboard.FarmTopBar
import com.sujoy.smartfarm.Presentation.Components.Dashboard.PrimaryButton
import com.sujoy.smartfarm.Presentation.Navigation.FarmerRoutes
import com.sujoy.smartfarm.Presentation.Utils.CropRecommend.districtItems
import com.sujoy.smartfarm.Presentation.Utils.CropRecommend.seasonItems
import com.sujoy.smartfarm.Presentation.Utils.CropRecommend.soilItems
import com.sujoy.smartfarm.Presentation.ViewModel.AppViewModel
import com.sujoy.smartfarm.ui.theme.GreenPrimary
import com.sujoy.smartfarm.ui.theme.OffWhite
import com.sujoy.smartfarm.ui.theme.OutlineGreen
import com.sujoy.smartfarm.ui.theme.TextPrimary
import com.sujoy.smartfarm.ui.theme.TextSecondary
import com.sujoy.smartfarm.ui.theme.WhitePure

@Composable
fun ProConCard(
    title: String,
    items: List<String>,
    isAdvantage: Boolean,
    accentColor: Color
) {
    val bgColor  = if (isAdvantage) Color(0xFFE8F5E9) else Color(0xFFFFF8E1)
    val dotColor = if (isAdvantage) Color(0xFF2E7D32) else Color(0xFFF57F17)
    val icon     = if (isAdvantage) "✅" else "⚠️"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(WhitePure)
            .border(1.dp, OutlineGreen, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(9.dp))
                    .background(bgColor),
                contentAlignment = Alignment.Center
            ) {
                Text(icon, fontSize = 16.sp)
            }
            Text(
                title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        HorizontalDivider(color = OutlineGreen.copy(alpha = 0.5f), thickness = 1.dp)
        Spacer(Modifier.height(10.dp))

        // Items
        items.forEachIndexed { index, item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .padding(top = 5.dp)
                        .size(7.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(dotColor)
                )
                Text(
                    text = item,
                    fontSize = 13.sp,
                    color = TextPrimary,
                    lineHeight = 19.sp,
                    modifier = Modifier.weight(1f)
                )
            }
            if (index < items.lastIndex) {
                HorizontalDivider(
                    color = OutlineGreen.copy(alpha = 0.3f),
                    thickness = 0.5.dp,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
        }
    }
}