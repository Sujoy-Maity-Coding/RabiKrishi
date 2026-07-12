package com.sujoy.smartfarm.Presentation.Utils.UpdateCrop

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.sujoy.smartfarm.Domain.model.DailyFarmUpdate
import com.sujoy.smartfarm.Presentation.Screens.CropUpdate.DailyUpdateCard
import com.sujoy.smartfarm.Presentation.ViewModel.AppViewModel

@Composable
fun TimelineItem(

    update: DailyFarmUpdate,

    showLine: Boolean,

    onClick: () -> Unit

) {

    val statusColor = when {

        update.pestFound ->
            Color.Red

        update.diseaseFound ->
            Color.Red

        update.leafColor == "Yellow" ->
            Color(0xFFFFC107)

        else ->
            Color(0xFF4CAF50)
    }

    Row(

        modifier = Modifier.fillMaxWidth(),

        horizontalArrangement =
        Arrangement.spacedBy(16.dp)

    ) {

        Column(

            horizontalAlignment =
            Alignment.CenterHorizontally

        ) {

            Box(

                modifier = Modifier
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(statusColor)
            )

            if(showLine){

                Box(

                    modifier = Modifier
                        .width(2.dp)
                        .height(170.dp)
                        .background(Color.LightGray)
                )
            }
        }

        DailyUpdateCard(

            modifier = Modifier.weight(1f),

            update = update,

            onClick = onClick
        )
    }
}