package com.sujoy.smartfarm.Presentation.Screens.FarmList

import android.widget.Toast
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.sujoy.smartfarm.Domain.model.Farm
import com.sujoy.smartfarm.Presentation.Components.Dashboard.FarmTopBar
import com.sujoy.smartfarm.Presentation.Navigation.FarmerRoutes
import com.sujoy.smartfarm.Presentation.Utils.CropRecommend.localizedDigits
import com.sujoy.smartfarm.Presentation.ViewModel.AppViewModel
import com.sujoy.smartfarm.R
import com.sujoy.smartfarm.ui.theme.GreenContainer
import com.sujoy.smartfarm.ui.theme.GreenPrimary
import com.sujoy.smartfarm.ui.theme.OffWhite
import com.sujoy.smartfarm.ui.theme.TextPrimary
import com.sujoy.smartfarm.ui.theme.TextSecondary
import com.sujoy.smartfarm.ui.theme.WhitePure
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyFarmsScreen(
    navController: NavHostController,
    appViewModel: AppViewModel = hiltViewModel()
) {
    val state by appViewModel.myFarmsState.collectAsState()

    LaunchedEffect(Unit) { appViewModel.getMyFarms() }

    Scaffold(
        containerColor = OffWhite,
        topBar = {
            FarmTopBar(
                title = stringResource(R.string.my_farms_title),
                showBack = true,
                onBack = { navController.popBackStack() }
            )
        }
    ) { padding ->

        Box(modifier = Modifier.fillMaxSize().padding(padding)) {

            when {

                // ── Loading
                state.isLoading -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(GreenContainer),
                            contentAlignment = Alignment.Center
                        ) { Text("🌾", fontSize = 32.sp) }
                        CircularProgressIndicator(color = GreenPrimary, strokeWidth = 3.dp)
                        Text(
                            stringResource(R.string.loading_farms),
                            fontSize = 14.sp,
                            color = TextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // ── Error
                state.error.isNotEmpty() -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("⚠️", fontSize = 44.sp)
                        Text(
                            stringResource(R.string.something_went_wrong),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            state.error,
                            fontSize = 13.sp,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // ── Empty
                state.farms.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(RoundedCornerShape(28.dp))
                                .background(GreenContainer),
                            contentAlignment = Alignment.Center
                        ) { Text("🌱", fontSize = 48.sp) }

                        Text(
                            stringResource(R.string.no_farms_yet),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            stringResource(R.string.no_farms_subtitle),
                            fontSize = 13.sp,
                            color = TextSecondary,
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp
                        )
                        Button(
                            onClick = {
                                navController.navigate(FarmerRoutes.CropRecommendationScreen)
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GreenPrimary,
                                contentColor   = WhitePure
                            )
                        ) {
                            Icon(
                                Icons.Outlined.Eco,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(stringResource(R.string.get_crop_recommendation_btn), fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                // ── Farm list
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            horizontal = 20.dp,
                            vertical = 16.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {

                        // Summary header
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(GreenPrimary)
                                    .padding(horizontal = 18.dp, vertical = 14.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            stringResource(R.string.your_farm_portfolio),
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = WhitePure
                                        )
                                        Text(
                                            stringResource(R.string.active_farms_count, state.farms.size),
                                            fontSize = 11.sp,
                                            color = WhitePure.copy(alpha = 0.75f),
                                            modifier = Modifier.padding(top = 2.dp)
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(WhitePure.copy(alpha = 0.15f))
                                            .padding(horizontal = 14.dp, vertical = 10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            localizedDigits("${state.farms.size}"),
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = WhitePure
                                        )
                                    }
                                }
                            }
                        }

                        // Section label
                        item {
                            Text(
                                stringResource(R.string.all_farms_label),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextSecondary,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }

                        // Farm cards
                        items(state.farms) { farm ->
                            FarmCard(
                                farm = farm,
                                onOpenFarm = {
                                    navController.navigate(
                                        FarmerRoutes.FarmDetailsScreen(
                                            farmId        = farm.farmId,
                                            cropId        = farm.cropId
                                        )
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}