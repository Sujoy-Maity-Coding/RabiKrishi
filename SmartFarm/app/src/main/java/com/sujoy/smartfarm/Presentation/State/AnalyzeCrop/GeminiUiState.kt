package com.sujoy.smartfarm.Presentation.State.AnalyzeCrop

import com.sujoy.smartfarm.AI.model.GeminiAnalysisResult

data class GeminiUiState(

    val isLoading: Boolean = false,

    val result: GeminiAnalysisResult? = null,

    val error: String = ""

)