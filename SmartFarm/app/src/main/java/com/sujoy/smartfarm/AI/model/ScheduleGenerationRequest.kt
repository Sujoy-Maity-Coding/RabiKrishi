package com.sujoy.smartfarm.AI.model

data class ScheduleGenerationRequest(
    val cropId: String = "",
    val cropName: String = "",
    val farmingMethod: String = "",
    val district: String = "",
    val season: String = "",
    val farmSize: Double = 0.0,
    val unit: String = "Acre",
    val languageCode: String = "en",
    val basePhaseJson: String = ""   // ← changed: ONE phase from the base schedule, not the whole thing
)