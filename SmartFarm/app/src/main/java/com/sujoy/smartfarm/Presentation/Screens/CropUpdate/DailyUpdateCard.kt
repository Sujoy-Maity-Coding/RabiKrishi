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
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.sujoy.smartfarm.Domain.model.DailyFarmUpdate
import com.sujoy.smartfarm.Presentation.Screens.FarmList.toDateString

@Composable
fun DailyUpdateCard(

    modifier: Modifier = Modifier,

    update: DailyFarmUpdate,

    onClick: () -> Unit

){
    val status = when {

        update.pestFound ->
            "🔴 Pest Detected"

        update.diseaseFound ->
            "🔴 Disease Found"

        update.leafColor == "Yellow" ->
            "🟡 Needs Attention"

        else ->
            "🟢 Healthy"
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

            Text("Day ${update.day}")

            Text(
                update.date.toDateString()
            )

            Row(

                horizontalArrangement =
                Arrangement.spacedBy(8.dp)

            ) {

                Text("🌱")

                Text("${update.plantHeight} cm")
            }

            Row(

                horizontalArrangement =
                Arrangement.spacedBy(8.dp)

            ) {

                Text("🍃")

                Text(update.leafColor)
            }

            Row(

                horizontalArrangement =
                Arrangement.spacedBy(8.dp)

            ) {

                Text("💧")

                Text(update.soilMoisture)
            }

            Row(

                horizontalArrangement =
                Arrangement.spacedBy(8.dp)

            ) {

                Text("🐛")

                Text(

                    if(update.pestFound)

                        "Pest Found"

                    else

                        "No Pest"
                )
            }
            HorizontalDivider()

            Spacer(
                Modifier.height(8.dp)
            )

            TextButton(

                onClick = onClick

            ) {

                Text("See Full Analysis →")
            }
        }
    }
}