package com.sujoy.smartfarm.Presentation.Components.Crop

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
import com.sujoy.smartfarm.Presentation.ViewModel.AppViewModel
import com.sujoy.smartfarm.ui.theme.GreenPrimary
import com.sujoy.smartfarm.ui.theme.WhitePure

@Composable
fun ResultHeroBanner(
    district: String,
    season: String,
    soilType: String,
    count: Int
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(GreenPrimary)
            .padding(horizontal = 20.dp, vertical = 22.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    "Analysis complete ✓",
                    fontSize = 11.sp,
                    color = WhitePure.copy(alpha = 0.7f),
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    "$district · $season",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = WhitePure
                )
                Text(
                    "$soilType soil · ${listOf("Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec").getOrElse(0){"–"}}",
                    fontSize = 13.sp,
                    color = WhitePure.copy(alpha = 0.75f)
                )
            }
            // Big count badge
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(WhitePure.copy(alpha = 0.15f))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "$count",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = WhitePure
                )
                Text("crops", fontSize = 11.sp, color = WhitePure.copy(alpha = 0.75f))
            }
        }
    }
}