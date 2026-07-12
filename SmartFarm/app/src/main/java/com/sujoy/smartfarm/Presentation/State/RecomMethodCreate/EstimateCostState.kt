package com.sujoy.smartfarm.Presentation.State.RecomMethodCreate

import com.sujoy.smartfarm.Domain.model.CropMethod.CostEstimation

data class EstimateCostState(

    val isLoading: Boolean = false,

    val results: Map<String, CostEstimation> = emptyMap(),

    val currentKey: String = "",

    val error: String = ""

)