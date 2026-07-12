package com.sujoy.smartfarm.Domain.model.AI

data class DiseasePredictionResponse(

    val diseaseName: String,

    val confidence: Float,

    val severity: String,

    val healthScore: Int,

    val recommendations: List<String>
)