package com.sujoy.smartfarm.Presentation.State.AnalyzeCrop

data class FarmUiState(

    val cropName: String = "Rice",

    val farmingMethod: String = "",

    val landArea: Double = 0.0,

    val currentPhase: String = "",

    val progress: Int = 0

)