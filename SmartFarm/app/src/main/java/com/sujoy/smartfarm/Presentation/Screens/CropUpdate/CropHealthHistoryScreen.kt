package com.sujoy.smartfarm.Presentation.Screens.CropUpdate

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.sujoy.smartfarm.Presentation.Utils.UpdateCrop.TimelineItem
import com.sujoy.smartfarm.Presentation.ViewModel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CropHealthHistoryScreen(

    farmId: String,

    navController: NavHostController,

    appViewModel: AppViewModel = hiltViewModel()

) {

    val state by appViewModel
        .cropHealthHistoryState
        .collectAsState()

    LaunchedEffect(Unit) {

        appViewModel.getDailyUpdates(
            farmId
        )
    }

    Scaffold(

        topBar = {

            TopAppBar(

                title = {

                    Text("Crop Health History")
                }
            )
        }

    ) { padding ->

        when {

            state.isLoading -> {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {

                    CircularProgressIndicator()
                }
            }

            state.error.isNotEmpty() -> {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {

                    Text(state.error)
                }
            }

            state.updates.isEmpty() -> {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {

                    Text("No updates available")
                }
            }

            else -> {

                LazyColumn(

                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),

                    verticalArrangement =
                    Arrangement.spacedBy(16.dp),

                    contentPadding =
                    PaddingValues(16.dp)

                ) {

                    itemsIndexed(
                        state.updates
                    ) { index, update ->

                        TimelineItem(

                            update = update,

                            showLine =
                            index != state.updates.lastIndex,

                            onClick = {

                                // Next screen
                            }
                        )
                    }
                }
            }
        }
    }
}

