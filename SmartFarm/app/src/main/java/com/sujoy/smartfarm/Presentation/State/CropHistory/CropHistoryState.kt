package com.sujoy.smartfarm.Presentation.State.CropHistory

import com.sujoy.smartfarm.Domain.model.DailyFarmUpdate

data class CropHistoryState(

    val isLoading: Boolean = false,

    val updates: List<DailyFarmUpdate> = emptyList(),

    val error: String = ""

)