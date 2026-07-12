package com.sujoy.smartfarm.Presentation.State

import com.sujoy.smartfarm.Domain.model.DailyFarmUpdate

data class CropHealthHistoryState(

    val isLoading: Boolean = false,

    val updates: List<DailyFarmUpdate> = emptyList(),

    val error: String = ""
)