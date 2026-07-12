package com.sujoy.smartfarm.Presentation.State.RecomMethodCreate

import com.sujoy.smartfarm.Domain.model.Crop

data class RecommendationState(

    val isLoading: Boolean = false,

    val crops: List<Crop> = emptyList(),

    val error: String = ""

)