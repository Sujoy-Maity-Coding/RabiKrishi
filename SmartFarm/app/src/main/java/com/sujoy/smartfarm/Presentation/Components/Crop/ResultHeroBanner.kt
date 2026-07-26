package com.sujoy.smartfarm.Presentation.Components.Crop

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sujoy.smartfarm.Presentation.Utils.CropRecommend.localizedDigits
import com.sujoy.smartfarm.R
import com.sujoy.smartfarm.ui.theme.GreenPrimary
import com.sujoy.smartfarm.ui.theme.WhitePure

@Composable
fun ResultHeroBanner(
    district: String,
    season: String,
    soilType: String,
    count: Int
) {
    val monthNames = stringArrayResource(R.array.month_short_names)

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
                    stringResource(R.string.analysis_complete),
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
                    "$soilType · ${monthNames.getOrElse(0) { "–" }}",
                    fontSize = 13.sp,
                    color = WhitePure.copy(alpha = 0.75f)
                )
            }
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(WhitePure.copy(alpha = 0.15f))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    localizedDigits("$count"),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = WhitePure
                )
                Text(stringResource(R.string.crops_label), fontSize = 11.sp, color = WhitePure.copy(alpha = 0.75f))
            }
        }
    }
}