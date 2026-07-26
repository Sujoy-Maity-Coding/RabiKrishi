package com.sujoy.smartfarm.Presentation.Screens.Dashboard

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.sujoy.smartfarm.Presentation.Components.Dashboard.FarmTopBar
import com.sujoy.smartfarm.Presentation.Components.Dashboard.FeatureCard
import com.sujoy.smartfarm.Presentation.Components.Dashboard.StatChip
import com.sujoy.smartfarm.Presentation.Navigation.FarmerRoutes
import com.sujoy.smartfarm.Presentation.ViewModel.AppViewModel
import com.sujoy.smartfarm.R
import com.sujoy.smartfarm.ui.theme.GreenPrimary
import com.sujoy.smartfarm.ui.theme.OffWhite
import com.sujoy.smartfarm.ui.theme.TextSecondary
import com.sujoy.smartfarm.ui.theme.WhitePure

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    appViewModel: AppViewModel = hiltViewModel()
) {
    Scaffold(
        containerColor = OffWhite,
        topBar = {
            FarmTopBar(title = stringResource(R.string.app_title_dashboard))
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 20.dp)
        ) {

            // Greeting hero banner
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(GreenPrimary)
                        .padding(24.dp)
                ) {
                    Column {
                        Text(text = stringResource(R.string.home_greeting), fontSize = 22.sp, fontWeight = FontWeight.Bold, color = WhitePure)
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = stringResource(R.string.home_subtitle),
                            style = MaterialTheme.typography.bodyMedium,
                            color = WhitePure.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            // Quick stats row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatChip(emoji = "📍", label = stringResource(R.string.stat_districts), modifier = Modifier.weight(1f))
                    StatChip(emoji = "🌿", label = stringResource(R.string.stat_seasons), modifier = Modifier.weight(1f))
                    StatChip(emoji = "🪨", label = stringResource(R.string.stat_soil_types), modifier = Modifier.weight(1f))
                }
            }

            // Section label
            item {
                Text(
                    text = stringResource(R.string.section_features),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = TextSecondary
                )
            }

            // Feature cards
            item {
                FeatureCard(
                    title = stringResource(R.string.feature_crop_title),
                    emoji = "🌾",
                    subtitle = stringResource(R.string.feature_crop_subtitle),
                    onClick = { navController.navigate(FarmerRoutes.CropRecommendationScreen) }
                )
            }

            item {
                FeatureCard(
                    title = stringResource(R.string.feature_farms_title),
                    emoji = "🚜",
                    subtitle = stringResource(R.string.feature_farms_subtitle),
                    onClick = {
                        navController.navigate(
                            FarmerRoutes.MyFarmsScreen
                        )
                    }
                )
            }

            item {
                FeatureCard(
                    title = stringResource(R.string.feature_profile_title),
                    emoji = "👤",
                    subtitle = stringResource(R.string.feature_profile_subtitle),
                    onClick = { navController.navigate(FarmerRoutes.ProfileScreen) }
                )
            }
        }
    }
}