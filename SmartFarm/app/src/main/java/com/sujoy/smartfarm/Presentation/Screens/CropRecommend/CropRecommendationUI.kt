package com.sujoy.smartfarm.Presentation.Screens.CropRecommend

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.sujoy.smartfarm.Presentation.Components.Dashboard.FarmTopBar
import com.sujoy.smartfarm.ui.theme.OffWhite

@Preview(showSystemUi = true)
@Composable
fun CropRecommendationUI(modifier: Modifier = Modifier) {
    Scaffold(
        containerColor = OffWhite,
        topBar = {
            FarmTopBar(title = "Crop Recommendation")
        }
    ) {padding ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(padding)) {
            Text(text = "")
        }
    }
}