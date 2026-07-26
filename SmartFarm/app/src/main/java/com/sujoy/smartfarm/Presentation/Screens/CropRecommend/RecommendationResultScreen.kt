package com.sujoy.smartfarm.Presentation.Screens.CropRecommend

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Landscape
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.sujoy.smartfarm.Presentation.Components.Crop.AnimatedCropCard
import com.sujoy.smartfarm.Presentation.Components.Crop.ContextChip
import com.sujoy.smartfarm.Presentation.Components.Crop.CropMeta
import com.sujoy.smartfarm.Presentation.Components.Crop.ErrorView
import com.sujoy.smartfarm.Presentation.Components.Crop.LoadingView
import com.sujoy.smartfarm.Presentation.Components.Crop.ResultHeroBanner
import com.sujoy.smartfarm.Presentation.Components.Crop.metaFor
import com.sujoy.smartfarm.Presentation.Components.Dashboard.FarmTopBar
import com.sujoy.smartfarm.Presentation.Utils.CropRecommend.localizedDigits
import com.sujoy.smartfarm.Presentation.Utils.CropRecommend.translatedDistrictName
import com.sujoy.smartfarm.Presentation.Utils.CropRecommend.translatedSeasonName
import com.sujoy.smartfarm.Presentation.Utils.CropRecommend.translatedSoilName
import com.sujoy.smartfarm.Presentation.ViewModel.AppViewModel
import com.sujoy.smartfarm.Presentation.ViewModel.GeminiViewModel
import com.sujoy.smartfarm.R
import com.sujoy.smartfarm.ui.theme.OffWhite
import com.sujoy.smartfarm.ui.theme.TextPrimary
import com.sujoy.smartfarm.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecommendationResultScreen(
    district: String,
    month: Int,
    season: String,
    soilType: String,
    navController: NavHostController,
    appViewModel: AppViewModel = hiltViewModel(),
    geminiViewModel: GeminiViewModel = hiltViewModel()
) {
    val state by geminiViewModel.recommendationState.collectAsState()

    LaunchedEffect(Unit) {
        geminiViewModel.getRecommendations(
            district = district, month = month, season = season, soilType = soilType
        )
    }

    Scaffold(
        containerColor = OffWhite,
        topBar = {
            FarmTopBar(
                title = stringResource(R.string.recommended_crops_title),
                showBack = true,
                onBack = { navController.popBackStack() }
            )
        }
    ) { padding ->

        Box(modifier = Modifier
            .fillMaxSize()
            .padding(padding)) {

            when {
                state.isLoading -> LoadingView()

                state.error.isNotEmpty() -> ErrorView(state.error)

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 32.dp),
                        verticalArrangement = Arrangement.spacedBy(0.dp)
                    ) {

                        // ── Hero summary banner
                        item {
                            ResultHeroBanner(
                                district = translatedDistrictName(district),
                                season = translatedSeasonName(season),
                                soilType = translatedSoilName(soilType),
                                count = state.crops.size
                            )
                        }

                        // ── Context chips
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp, vertical = 14.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                ContextChip(icon = Icons.Outlined.LocationOn, label = translatedDistrictName(district))
                                ContextChip(icon = Icons.Outlined.WbSunny,    label = translatedSeasonName(season))
                                ContextChip(icon = Icons.Outlined.Landscape,  label = translatedSoilName(soilType))
                            }
                        }

                        // ── Best match label
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    stringResource(R.string.best_matches_label),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    localizedDigits(stringResource(R.string.crops_count, state.crops.size)),
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }
                            Spacer(Modifier.height(12.dp))
                        }

                        // ── Crop cards
                        itemsIndexed(state.crops) { index, crop ->
                            val baseMeta = metaFor(crop)

                            val meta = CropMeta(

                                emoji = baseMeta.emoji,

                                accentColor = baseMeta.accentColor

                            )

                            AnimatedCropCard(

                                crop = crop,

                                meta = meta,

                                rank = index + 1,

                                district = district,

                                season = season,

                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp),

                                navController = navController

                            )
                        }
                    }
                }
            }
        }
    }
}