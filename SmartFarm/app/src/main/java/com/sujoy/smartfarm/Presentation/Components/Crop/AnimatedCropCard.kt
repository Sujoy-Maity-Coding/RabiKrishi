package com.sujoy.smartfarm.Presentation.Components.Crop

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.sujoy.smartfarm.Domain.model.Crop

@Composable
fun AnimatedCropCard(
    crop: Crop,
    meta: CropMeta,
    rank: Int,
    district: String,    // ← new
    season: String,
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay((rank * 80).toLong())
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInVertically(initialOffsetY = { it / 3 }),
        modifier = modifier
    ) {
        CropCard(crop = crop, meta = meta, rank = rank, district = district, season = season, navController=navController)
    }
}