package com.sujoy.smartfarm.Presentation.State.AnalyzeCrop

import com.sujoy.smartfarm.Domain.model.DailyFarmUpdate

data class LatestAIState(

    val isLoading: Boolean = false,

    val update: DailyFarmUpdate? = null,

    val error: String = ""

)