package com.sujoy.smartfarm.Presentation.Screens.CropUpdate

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.sujoy.smartfarm.Domain.model.DailyFarmUpdate
import com.sujoy.smartfarm.Presentation.Screens.FarmList.toDateString
import com.sujoy.smartfarm.Presentation.Utils.CropRecommend.localizedDigits
import com.sujoy.smartfarm.Presentation.Utils.FarmDetails.translatedLeafColor
import com.sujoy.smartfarm.Presentation.Utils.FarmDetails.translatedSoilMoisture
import com.sujoy.smartfarm.R

@Composable
fun DailyUpdateCard(

    modifier: Modifier = Modifier,

    update: DailyFarmUpdate,

    onClick: () -> Unit

){
    val status = when {
        update.pestFound -> stringResource(R.string.status_pest_detected)
        update.diseaseFound -> stringResource(R.string.status_disease_found)
        update.leafColor == "Yellow" -> stringResource(R.string.status_needs_attention)
        else -> stringResource(R.string.status_healthy)
    }
    Card(modifier = modifier) {

        Column(

            modifier = Modifier.padding(16.dp)
        ){
            Text(

                text = status,

                style = MaterialTheme.typography.titleMedium
            )
            AsyncImage(

                model = update.imageUrl,

                contentDescription = null,

                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            )

            Text(localizedDigits(stringResource(R.string.day_label, update.day)))

            Text(
                update.date.toDateString()
            )

            Row(

                horizontalArrangement =
                Arrangement.spacedBy(8.dp)

            ) {

                Text("🌱")

                Text("${localizedDigits("${update.plantHeight}")} cm")
            }

            Row(

                horizontalArrangement =
                Arrangement.spacedBy(8.dp)

            ) {

                Text("🍃")

                Text(translatedLeafColor(update.leafColor))
            }

            Row(

                horizontalArrangement =
                Arrangement.spacedBy(8.dp)

            ) {

                Text("💧")

                Text(translatedSoilMoisture(update.soilMoisture))
            }

            Row(

                horizontalArrangement =
                Arrangement.spacedBy(8.dp)

            ) {

                Text("🐛")

                Text(if (update.pestFound) stringResource(R.string.pest_found_label) else stringResource(R.string.no_pest_label))
            }
            HorizontalDivider()

            Spacer(
                Modifier.height(8.dp)
            )

            TextButton(

                onClick = onClick

            ) {

                Text(stringResource(R.string.see_full_analysis_btn))
            }
        }
    }
}